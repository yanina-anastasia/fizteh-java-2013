package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DistributedTable extends BasicTable {

    protected final int partsNumber = 16;
    protected File[] directoriesList = new File[partsNumber];
    protected File[][] filesList = new File[partsNumber][partsNumber];

    int unsavedChanges() {
        return changes.size();
    }

    private byte getFirstByte(String s) {
        try {
            return (byte) Math.abs(s.getBytes("UTF-8")[0]);
        } catch (UnsupportedEncodingException e) {
            return 0;
        }
    }

    DistributedTable(File tableDirectory, String name) throws IOException {
        currentPath = new File(tableDirectory.getPath() + File.separator + name);
        tableName = name;
        if (!currentPath.exists()) {
            if (!currentPath.mkdir()) {
                throw new IOException(currentPath.getAbsolutePath() + ": couldn't create directory");
            }
        }
        oldRecordNumber = 0;
        for (int i = 0; i < partsNumber; i++) {
            directoriesList[i] = new File(currentPath.getPath() + File.separator + Integer.toString(i) + ".dir");
            for (int j = 0; j < partsNumber; j++) {
                currentFile = new File(directoriesList[i].getPath() + File.separator + Integer.toString(j) + ".dat");
                filesList[i][j] = currentFile;
                if (directoriesList[i].exists() && currentFile.exists()) {
                    DataInputStream inputStream = new DataInputStream(new FileInputStream(currentFile));
                    String[] pair;
                    while ((pair = readNextPair(inputStream)) != null) {
                        byte firstByte = getFirstByte(pair[0]);
                        if (firstByte % partsNumber != i || (firstByte / partsNumber) % partsNumber != j) {
                            throw new IOException("Invalid key in file " + currentFile.getAbsolutePath());
                        }
                        oldRecordNumber++;
                    }
                    if (inputStream.read() != -1) {
                        throw new IOException("invalid file " + currentFile.getAbsolutePath());
                    }
                    inputStream.close();
                }
            }
        }
        changes = new HashMap<>();
        rollback();
    }

    @Override
     public String get(String key) throws IllegalArgumentException {
        byte firstByte = getFirstByte(key);
        currentFile = filesList[firstByte % partsNumber][(firstByte / partsNumber) % partsNumber];
        currentPath = directoriesList[firstByte % partsNumber];
        return super.get(key);
    }

    @Override
    public String put(String key, String value) throws IllegalArgumentException {
            byte firstByte = getFirstByte(key);
            currentFile = filesList[firstByte % partsNumber][(firstByte / partsNumber) % partsNumber];
            currentPath = directoriesList[firstByte % partsNumber];
            return super.put(key, value);
    }

    @Override
    public int commit() {
        int updated = changes.size();
        DataInputStream[][] inputStreams = new DataInputStream[partsNumber][partsNumber];
        DataOutputStream[][] outputStreams = new DataOutputStream[partsNumber][partsNumber];
        try {
            for (int i = 0; i < partsNumber; i++) {
                if (!directoriesList[i].exists()) {
                    if (!directoriesList[i].mkdir()) {
                        throw new IOException("couldn't create directory " + directoriesList[i].getAbsolutePath());
                    }
                }
                for (int j = 0; j < partsNumber; j++) {
                    if (!filesList[i][j].exists()) {
                        if (!filesList[i][j].createNewFile()) {
                            throw new IOException("couldn't create file " + filesList[i][j].getAbsolutePath());
                        }
                    }
                    if (!filesList[i][j].renameTo(new File(filesList[i][j].getPath() + "~"))) {
                        throw new IOException("couldn't rename file " + filesList[i][j].getAbsolutePath());
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
                    writeNextPair(outputStream, entry.getKey(), entry.getValue());
                }
            }
            for (int i = 0; i < partsNumber; i++) {
                for (int j = 0; j < partsNumber; j++) {
                    inputStreams[i][j].close();
                    outputStreams[i][j].close();
                    if (!(new File(filesList[i][j].getPath() + "~")).delete()) {
                        throw new IOException("couldn't delete file " + filesList[i][j].getPath() + "~");
                    }
                    if (filesList[i][j].length() == 0) {
                        if (!filesList[i][j].delete()) {
                            throw new IOException("couldn't delete file " + filesList[i][j].getPath());
                        }
                    }
                }
            }
            changes.clear();
            oldRecordNumber = recordNumber;
            for (File directory : directoriesList) {
                if (directory.list().length == 0) {
                    if (!directory.delete()) {
                        throw new IOException("couldn't delete directory " + directory.getAbsolutePath());
                    }
                }
            }
            return updated;
        } catch (IOException e) {
            for (int i = 0; i < partsNumber; i++) {
                for (int j = 0; j < partsNumber; j++) {
                    try {
                        if (inputStreams[i][j] != null) {
                            inputStreams[i][j].close();
                        }
                    } catch (IOException exception) {
                        printMessage(tableName + exception.getMessage());
                    }
                    try {
                        if (outputStreams[i][j] != null) {
                            outputStreams[i][j].close();
                        }
                    } catch (IOException exception) {
                        printMessage(tableName + exception.getMessage());
                    }
                }
            }
        }
        printMessage(tableName + ": cannot commit changes: i/o error occurred");
        return 0;
    }
}
