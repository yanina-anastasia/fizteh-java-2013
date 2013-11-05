package ru.fizteh.fivt.students.dzvonarev.filemap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MyTable implements Table {

    public class ValueNode {
        String oldValue;
        String newValue;
    }

    public MyTable(String newTableName) {
        tableName = newTableName;
        fileMap = new HashMap<>();
        changesMap = new HashMap<>();
        size = 0;
    }

    private String tableName; // current table
    private HashMap<String, String> fileMap;
    private HashMap<String, ValueNode> changesMap;
    private int size;


    public void modifyFileMap() {
        if (changesMap == null || changesMap.isEmpty()) {
            return;
        }
        Set fileSet = changesMap.entrySet();
        Iterator<Map.Entry<String, ValueNode>> i = fileSet.iterator();
        while (i.hasNext()) {
            Map.Entry<String, ValueNode> currItem = i.next();
            ValueNode value = currItem.getValue();
            if (!Equals(value.newValue, value.oldValue)) {
                if (value.newValue == null) {
                    fileMap.remove(currItem.getKey());
                } else {
                    fileMap.put(currItem.getKey(), value.newValue);
                }
            }
        }
    }

    public int countSize() {
        if (changesMap == null || changesMap.isEmpty()) {
            return 0;
        }
        size = 0;
        Set fileSet = changesMap.entrySet();
        Iterator<Map.Entry<String, ValueNode>> i = fileSet.iterator();
        while (i.hasNext()) {
            Map.Entry<String, ValueNode> currItem = i.next();
            ValueNode value = currItem.getValue();
            if (value.oldValue == null && value.newValue != null) {
                ++size;
                continue;
            }
            if (value.oldValue != null && value.newValue == null) {
                --size;
                continue;
            }
        }
        return size;
    }

    public int getCountOfChanges() {
        if (changesMap == null || changesMap.isEmpty()) {
            return 0;
        }
        Set fileSet = changesMap.entrySet();
        Iterator<Map.Entry<String, ValueNode>> i = fileSet.iterator();
        int counter = 0;
        while (i.hasNext()) {
            Map.Entry<String, ValueNode> currItem = i.next();
            ValueNode value = currItem.getValue();
            if (!Equals(value.newValue, value.oldValue)) {
                ++counter;
            }
        }
        return counter;
    }

    public boolean Equals(String st1, String st2) {
        if (st1 == null && st2 == null) {
            return true;
        }
        if (st1 == null) {
            return false;
        }
        return st1.equals(st2);
    }

    public void readFileMap() throws RuntimeException, IOException {
        String[] dbDirs = (new File(tableName)).list();
        if (dbDirs != null && dbDirs.length != 0) {
            for (String dbDir : dbDirs) {
                if (!isValidDir(dbDir)) {
                    throw new RuntimeException("directory " + dbDir + " is not valid");
                }
                File dbDirTable = new File(tableName + File.separator + dbDir);
                String[] dbDats = dbDirTable.list();
                if (dbDats == null || dbDats.length == 0) {
                    throw new RuntimeException("reading directory: " + tableName + " is not valid");
                }
                for (String dbDat : dbDats) {
                    String str = tableName + File.separator + dbDir + File.separator + dbDat;
                    readMyFileMap(str, dbDir, dbDat);
                }
            }
        }
    }

    /* READING FILEMAP */
    public void readMyFileMap(String fileName, String dir, String file) throws IOException, RuntimeException {
        RandomAccessFile fileReader = openFileForRead(fileName);
        long endOfFile = fileReader.length();
        long currFilePosition = fileReader.getFilePointer();
        if (endOfFile == 0) {
            closeFile(fileReader);
            throw new RuntimeException("reading directory: " + dir + " is not valid");
        }
        while (currFilePosition != endOfFile) {
            int keyLen = fileReader.readInt();
            int valueLen = fileReader.readInt();
            if (keyLen <= 0 || valueLen <= 0) {
                closeFile(fileReader);
                throw new RuntimeException(fileName + " : file is broken");
            }
            byte[] keyByte;
            byte[] valueByte;
            try {
                keyByte = new byte[keyLen];
                valueByte = new byte[valueLen];
            } catch (OutOfMemoryError e) {
                closeFile(fileReader);
                throw new RuntimeException(fileName + " : file is broken");
            }
            fileReader.readFully(keyByte, 0, keyLen);
            fileReader.readFully(valueByte, 0, valueLen);
            String key = new String(keyByte);
            String value = new String(valueByte);
            if (!keyIsValid(key, dir, file)) {
                closeFile(fileReader);
                throw new RuntimeException("file " + file + " in " + dir + " is not valid");
            }
            fileMap.put(key, value);
            currFilePosition = fileReader.getFilePointer();
            endOfFile = fileReader.length();
        }
        closeFile(fileReader);
    }

    public boolean keyIsValid(String key, String dir, String file) {
        int b = key.getBytes()[0];
        int nDirectory = Math.abs(b) % 16;
        int nFile = Math.abs(b) / 16 % 16;
        String rightDir = Integer.toString(nDirectory) + ".dir";
        String rightFile = Integer.toString(nFile) + ".dat";
        return (dir.equals(rightDir) && file.equals(rightFile));
    }

    public boolean isFilesInDirValid(String dirName) {
        File dir = new File(dirName);
        String[] file = dir.list();
        if (file == null || file.length == 0) {
            return true;
        }
        for (String currFile : file) {
            if (new File(dirName + File.separator + currFile).isHidden()) {
                continue;
            }
            if (new File(dirName + File.separator + currFile).isDirectory()) {
                return false;
            }
            if (!currFile.matches("[0-9][.]dat|1[0-5][.]dat")) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidDir(String path) {
        File dir = new File(path);
        String[] file = dir.list();
        if (file == null || file.length == 0) {
            return true;
        }
        for (String currFile : file) {
            if (new File(path + File.separator + currFile).isHidden()) {
                continue;
            }
            if (new File(path + File.separator + currFile).isDirectory() && currFile.matches("[0-9][.]dir|1[0-5][.]dir")) {
                if (!isFilesInDirValid(path + File.separator + currFile)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public void writeInTable() throws IOException {
        if (fileMap == null) {
            return;
        } else {
            if (fileMap.isEmpty()) {
                return;
            }
        }
        Set fileSet = fileMap.entrySet();
        Iterator<Map.Entry<String, String>> i = fileSet.iterator();
        while (i.hasNext()) {
            Map.Entry<String, String> currItem = i.next();
            String key = currItem.getKey();
            String value = currItem.getValue();
            int b = key.getBytes()[0];
            int nDirectory = Math.abs(b) % 16;
            int nFile = Math.abs(b) / 16 % 16;
            String rightDir = Integer.toString(nDirectory) + ".dir";
            String rightFile = Integer.toString(nFile) + ".dat";
            String path = tableName +
                    File.separator + rightDir + File.separator + rightFile;
            String dir = tableName +
                    File.separator + rightDir;
            File file = new File(path);
            File fileDir = new File(dir);
            if (!fileDir.exists()) {
                if (!fileDir.mkdir()) {
                    throw new IOException("can't create directory " + dir);
                }
            }
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new IOException("can't create file " + path);
                }
            }
            writeInFile(path, key, value);
        }
    }

    public void writeInFile(String path, String key, String value) throws IOException {
        RandomAccessFile fileWriter = openFileForWrite(path);
        fileWriter.skipBytes((int) fileWriter.length());
        try {
            if (key == null || value == null) {
                closeFile(fileWriter);
                throw new IOException("updating file: error in writing");
            }
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
            fileWriter.writeInt(keyBytes.length);
            fileWriter.writeInt(valueBytes.length);
            fileWriter.write(keyBytes);
            fileWriter.write(valueBytes);
        } catch (IOException e) {
            throw new IOException("updating file: error in writing");
        } finally {
            closeFile(fileWriter);
        }
    }

    public RandomAccessFile openFileForRead(String fileName) throws IOException {
        RandomAccessFile newFile;
        try {
            newFile = new RandomAccessFile(fileName, "rw");
        } catch (FileNotFoundException e) {
            throw new IOException("reading from file: file not found");
        }
        return newFile;
    }

    public RandomAccessFile openFileForWrite(String fileName) throws IOException {
        RandomAccessFile newFile;
        try {
            newFile = new RandomAccessFile(fileName, "rw");
        } catch (FileNotFoundException e) {
            throw new IOException("writing to file: file not found");
        }
        return newFile;
    }

    public void closeFile(RandomAccessFile file) throws IOException {
        try {
            file.close();
        } catch (IOException e) {
            throw new IOException("error in closing file");
        }
    }

    @Override
    public String getName() {
        return tableName.substring(tableName.lastIndexOf(File.separator) + 1, tableName.length());
    }

    @Override
    public String get(String key) throws IllegalArgumentException {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("get: wrong key");
        }
        if (changesMap.containsKey(key)) {            // если он был изменен
            return changesMap.get(key).newValue;
        } else {
            if (fileMap.containsKey(key)) {
                return fileMap.get(key);
            } else {
                return null;
            }
        }
    }


    public void addChanges(String key, String value) {
        if (changesMap.containsKey(key)) {
            changesMap.get(key).newValue = value;
        } else {
            ValueNode valueNode = new ValueNode();
            valueNode.oldValue = fileMap.get(key);
            valueNode.newValue = value;
            changesMap.put(key, valueNode);
        }
    }

    @Override
    public String put(String key, String value) throws IllegalArgumentException {
        if (key == null || value == null || key.trim().isEmpty() || value.trim().isEmpty()) {
            throw new IllegalArgumentException("put: wrong key and value");
        }
        String oldValue = get(key);
        addChanges(key, value);
        return oldValue;
    }

    @Override
    public String remove(String key) throws IllegalArgumentException {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("remove: wrong key");
        }
        String oldValue = get(key);
        if (oldValue != null) {
            addChanges(key, null);
        }
        return oldValue;
    }

    @Override
    public int size() {
        return countSize() + fileMap.size();
    }

    @Override
    public int commit() {
        modifyFileMap();
        int count = getCountOfChanges();
        changesMap.clear();
        return count;
    }

    @Override
    public int rollback() {
        int count = getCountOfChanges();
        changesMap.clear();
        return count;
    }

}
