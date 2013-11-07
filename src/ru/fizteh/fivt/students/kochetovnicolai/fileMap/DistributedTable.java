package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kochetovnicolai.shell.FileManager;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.nio.charset.StandardCharsets;

public class DistributedTable extends FileManager implements Table {

    protected File currentFile;
    protected String tableName;
    protected HashMap<String, String> cache;
    protected HashMap<String, String> changes;
    protected int recordNumber;
    protected int oldRecordNumber;

    protected final int partsNumber = 16;
    protected File[] directoriesList = new File[partsNumber];
    protected File[][] filesList = new File[partsNumber][partsNumber];

    @Override
    public String getName() {
        return tableName;
    }

    private byte getFirstByte(String s) {
        return (byte) Math.abs(s.getBytes(StandardCharsets.UTF_8)[0]);
    }

    public boolean isValidKey(String key) {
        return key != null && !key.equals("") && !key.matches("[\\s]*");
    }

    public boolean isValidValue(String value) {
        return isValidKey(value);
    }

    private int getCurrentFileLength(int dirNumber, int fileNumber) throws IOException {
        int fileRecordNumber = 0;
        currentFile = filesList[dirNumber][fileNumber];
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(currentFile))) {
            String[] pair;
            try {
                while ((pair = readNextPair(inputStream)) != null) {
                    byte firstByte = getFirstByte(pair[0]);
                    if (firstByte % partsNumber != dirNumber || (firstByte / partsNumber) % partsNumber != fileNumber) {
                        throw new IOException("invalid key format");
                    }
                    fileRecordNumber++;
                }
            } catch (IOException e) {
                throw new IOException(currentFile.getPath() + ": " + e.getMessage());
            }
        }
        return fileRecordNumber;
    }

    protected int readTable() throws IOException {
        int directoriesNumber = 0;
        int tableSize = 0;
        for (int i = 0; i < partsNumber; i++) {
            if (directoriesList[i].exists()) {
                directoriesNumber++;
                int filesNumber = 0;
                for (int j = 0; j < partsNumber; j++) {
                    if (filesList[i][j].exists()) {
                        filesNumber++;
                        tableSize += getCurrentFileLength(i, j);
                    }
                }
                if (directoriesList[i].list().length != filesNumber) {
                    throw new IOException(directoriesList[i].getPath() + ": contains unknown files or directories");
                }
            }
        }
        if (currentPath.list().length != directoriesNumber) {
            throw new IOException(currentPath.getPath() + ": contains unknown files or directories");
        }
        return tableSize;
    }

    public DistributedTable(File tableDirectory, String name) throws IOException {
        currentPath = new File(tableDirectory.getPath() + File.separator + name);
        tableName = name;
        if (!currentPath.exists()) {
            if (!currentPath.mkdir()) {
                throw new IOException(currentPath.getAbsolutePath() + ": couldn't create directory");
            }
        }
        for (int i = 0; i < partsNumber; i++) {
            directoriesList[i] = new File(currentPath.getPath() + File.separator + i + ".dir");
            for (int j = 0; j < partsNumber; j++) {
                filesList[i][j] = new File(directoriesList[i].getPath() + File.separator + j + ".dat");
            }
        }
        oldRecordNumber = readTable();
        cache = new HashMap<>();
        changes = new HashMap<>();
        rollback();
    }

    @Override
    public int rollback() {
        int canceled = findDifference();
        recordNumber = oldRecordNumber;
        changes.clear();
        return canceled;
    }

    @Override
     public String get(String key) throws IllegalArgumentException {
        byte firstByte = getFirstByte(key);
        currentFile = filesList[firstByte % partsNumber][(firstByte / partsNumber) % partsNumber];
        currentPath = directoriesList[firstByte % partsNumber];
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (changes.containsKey(key)) {
            return changes.get(key);
        } else if (cache.containsKey(key)) {
            return cache.get(key);
        } else {
            try {
                cache.put(key, readValue(key));
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage());
            }
            return cache.get(key);
        }
    }

    @Override
    public String put(String key, String value) throws IllegalArgumentException {
        byte firstByte = getFirstByte(key);
        currentFile = filesList[firstByte % partsNumber][(firstByte / partsNumber) % partsNumber];
        currentPath = directoriesList[firstByte % partsNumber];
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (get(key) == null) {
            recordNumber++;
        }
        return changes.put(key, value);
    }

    protected int findDifference() {
        int diff = 0;
        for (String key : changes.keySet()) {
            if (changes.get(key) == null) {
                if (cache.containsKey(key) && cache.get(key) != null) {
                    diff++;
                }
            } else {
                if (!cache.containsKey(key) || !changes.get(key).equals(cache.get(key))) {
                    diff++;
                }
            }
        }
        return diff;
    }

    @Override
    public int commit() {
        int updated = findDifference();
        for (String key : changes.keySet()) {
            cache.put(key, changes.get(key));
        }
        changes.clear();
        for (String key : cache.keySet()) {
            changes.put(key, cache.get(key));
        }
        boolean isClosed = true;
        DataInputStream[][] inputStreams = new DataInputStream[partsNumber][partsNumber];
        DataOutputStream[][] outputStreams = new DataOutputStream[partsNumber][partsNumber];
        try {
            for (int i = 0; i < partsNumber; i++) {
                if (!directoriesList[i].exists()) {
                    if (!directoriesList[i].mkdir()) {
                        throw new IOException(directoriesList[i].getPath() + ": couldn't create directory");
                    }
                }
                for (int j = 0; j < partsNumber; j++) {
                    if (!filesList[i][j].exists()) {
                        if (!filesList[i][j].createNewFile()) {
                            throw new IOException(filesList[i][j].getAbsolutePath() + ": couldn't create file");
                        }
                    }
                    if (!filesList[i][j].renameTo(new File(filesList[i][j].getPath() + "~"))) {
                        throw new IOException(filesList[i][j].getAbsolutePath() + ": couldn't rename file");
                    }
                    inputStreams[i][j] = new DataInputStream(new FileInputStream(filesList[i][j].getPath() + "~"));
                    outputStreams[i][j] = new DataOutputStream(new FileOutputStream(filesList[i][j]));
                    DataInputStream inputStream = inputStreams[i][j];
                    DataOutputStream outputStream = outputStreams[i][j];
                    String nextKey;
                    String nextValue;
                    String[] pair;
                    while ((pair = readNextPair(inputStream)) != null) {
                        nextKey = pair[0];
                        nextValue = pair[1];
                        if (changes.containsKey(nextKey)) {
                            nextValue = changes.get(nextKey);
                            changes.remove(nextKey);
                        }
                        if (nextValue != null) {
                            writeNextPair(outputStream, nextKey, nextValue);
                        }
                    }
                }
            }
            Set<Map.Entry<String, String>> entries = changes.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                if (entry.getValue() != null) {
                    byte firstByte = getFirstByte(entry.getKey());
                    DataOutputStream outputStream = outputStreams[firstByte % partsNumber]
                            [(firstByte / partsNumber) % partsNumber];
                    try {
                        writeNextPair(outputStream, entry.getKey(), entry.getValue());
                    } catch (IOException e) {
                        throw new IllegalStateException(e.getMessage());
                    }
                }
            }
            for (int i = 0; i < partsNumber; i++) {
                for (int j = 0; j < partsNumber; j++) {
                    inputStreams[i][j].close();
                    outputStreams[i][j].close();
                    if (!(new File(filesList[i][j].getPath() + "~")).delete()) {
                        throw new IllegalStateException(filesList[i][j].getPath() + "~: couldn't delete file");
                    }
                    if (filesList[i][j].length() == 0) {
                        if (!filesList[i][j].delete()) {
                            throw new IllegalStateException(filesList[i][j].getPath() + ": couldn't delete file");
                        }
                    }
                }
            }
            changes.clear();
            oldRecordNumber = recordNumber;
            for (File directory : directoriesList) {
                if (directory.list().length == 0) {
                    if (!directory.delete()) {
                        throw new IllegalStateException(directory.getAbsolutePath() + ": couldn't delete directory");
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        } finally {
            for (int i = 0; i < partsNumber; i++) {
                for (int j = 0; j < partsNumber; j++) {
                    try {
                        if (inputStreams[i][j] != null) {
                            inputStreams[i][j].close();
                        }
                    } catch (Exception e) {
                        isClosed = false;
                    }
                    try {
                        if (outputStreams[i][j] != null) {
                            outputStreams[i][j].close();
                        }
                    } catch (Exception e) {
                        isClosed = false;
                    }
                }
            }
        }
        if (!isClosed) {
            throw new IllegalStateException("couldn't close some files");
        }
        return updated;
    }

    @Override
    public String remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (get(key) != null) {
            recordNumber--;
            return changes.put(key, null);
        }
        return get(key);
    }

    @Override
    public int size() {
        return recordNumber;
    }

    protected void writeNextPair(DataOutputStream outputStream, String key, String value) throws IOException {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
        outputStream.writeInt(keyBytes.length);
        outputStream.writeInt(valueBytes.length);
        outputStream.write(keyBytes);
        outputStream.write(valueBytes);
    }

    protected String[] readNextPair(DataInputStream inputStream) throws IOException {
        if (inputStream.available() == 0) {
            return null;
        }
        int keySize;
        int valueSize;
        try {
            keySize = inputStream.readInt();
            valueSize = inputStream.readInt();
        } catch (IOException e) {
            throw new EOFException("the file is corrupt or has an incorrect format");
        }
        if (keySize < 1 || valueSize < 1 || inputStream.available() < keySize
                || inputStream.available() < valueSize || inputStream.available() < keySize + valueSize) {
            throw new EOFException("the file is corrupt or has an incorrect format");
        }
        byte[] keyBytes = new byte[keySize];
        byte[] valueBytes = new byte[valueSize];
        if (inputStream.read(keyBytes) != keySize || inputStream.read(valueBytes) != valueSize) {
            throw new EOFException("the file is corrupt or has an incorrect format");
        }
        String[] pair = new String[2];
        pair[0] = new String(keyBytes, StandardCharsets.UTF_8);
        pair[1] = new String(valueBytes, StandardCharsets.UTF_8);
        return pair;
    }

    protected String readValue(String key) throws IOException {
        if (currentFile == null || !currentFile.exists()) {
            return null;
        }
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(currentFile))) {
            String[] pair;
            while ((pair = readNextPair(inputStream)) != null) {
                if (pair[0].equals(key)) {
                    inputStream.close();
                    return pair[1];
                }
            }
        }
        return null;
    }

    public void clear() throws IOException {
        for (int i = 0; i < partsNumber; i++) {
            for (int j = 0; j < partsNumber; j++) {
                if (filesList[i][j].exists() && !filesList[i][j].delete()) {
                    throw new IOException(filesList[i][j].getPath() + ": couldn't remove file");
                }
            }
            if (directoriesList[i].exists() && !directoriesList[i].delete()) {
                throw new IOException(directoriesList[i].getPath() + ": couldn't remove directory");
            }
        }
    }
}
