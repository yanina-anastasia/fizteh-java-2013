package ru.fizteh.fivt.students.annasavinova.filemap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.storage.strings.Table;

public class DataBase implements Table {
    protected HashMap<String, String> dataMap = new HashMap<>();
    protected HashMap<String, String> oldDataMap = new HashMap<>();
    private String currTable = "";
    private static String rootDir = "";
    private boolean hasLoadedData = false;

    public DataBase(String tableName, String root) throws IllegalArgumentException, IllegalStateException {
        if (tableName == null) {
            IllegalArgumentException e = new IllegalArgumentException("table name is null");
            throw e;
        } else {
            currTable = tableName;
        }
        if (root == null || root.isEmpty()) {
            throw new IllegalArgumentException("Root name is empty");
        }
        if (!(new File(root).exists())) {
            throw new IllegalStateException("Root not exists");
        }
        if (root.endsWith(File.separator)) {
            rootDir = root;
        } else {
            rootDir = root + File.separatorChar;
        }
        if (!(new File(rootDir + tableName).exists())) {
            throw new IllegalStateException("Table not exists");
        }
        loadData();
    }

    protected File getDirWithNum(int dirNum) {
        File res = new File(rootDir + currTable + File.separatorChar + dirNum + ".dir");
        return res;
    }

    protected File getFileWithNum(int fileNum, int dirNum) {
        File res = new File(rootDir + currTable + File.separatorChar + dirNum + ".dir" + File.separatorChar + fileNum
                + ".dat");
        return res;
    }

    protected void loadData() throws RuntimeException {
        if (!hasLoadedData) {
            for (int i = 0; i < 16; ++i) {
                File currentDir = getDirWithNum(i);
                if (!currentDir.exists()) {
                    if (!currentDir.mkdir()) {
                        throw new RuntimeException("Cannot create new directory");
                    }
                } else if (!currentDir.isDirectory()) {
                    throw new RuntimeException("Incorrect files in table");
                }
                for (int j = 0; j < 16; ++j) {
                    File currentFile = getFileWithNum(j, i);
                    if (!currentFile.exists()) {
                        try {
                            currentFile.createNewFile();
                        } catch (IOException e) {
                            throw new RuntimeException("Cannot create new file", e);
                        }
                    }
                    loadFile(currentFile, i, j);
                }
            }
            hasLoadedData = true;
        }
    }

    protected void unloadData() throws RuntimeException {
        if (hasLoadedData) {
            unloadMap();
            for (int i = 0; i < 16; ++i) {
                File currentDir = getDirWithNum(i);
                if (currentDir.list().length == 0) {
                    if (!currentDir.delete()) {
                        throw new RuntimeException("Cannot unload data");
                    }
                }
            }
            hasLoadedData = false;
        }
    }

    protected void loadKeyAndValue(RandomAccessFile dataFile, int dirNum, int fileNum) throws RuntimeException {
        try {
            int keyLong = dataFile.readInt();
            int valueLong = dataFile.readInt();
            if (keyLong <= 0 || valueLong <= 0) {
                throw new RuntimeException("Cannot Load File1");
            } else {
                byte[] keyBytes = new byte[keyLong];
                byte[] valueBytes = new byte[valueLong];
                dataFile.read(keyBytes);
                dataFile.read(valueBytes);
                byte b = 0;
                b = (byte) Math.abs(keyBytes[0]);
                int ndirectory = b % 16;
                int nfile = b / 16 % 16;
                if (ndirectory != dirNum || nfile != fileNum) {
                    throw new RuntimeException("Incorrect input");
                }
                String key = new String(keyBytes);
                String value = new String(valueBytes);
                dataMap.put(key, value);
                oldDataMap.put(key, value);
            }
        } catch (IOException | OutOfMemoryError e) {
            throw new RuntimeException("Cannot Load File3", e);
        }
    }

    protected void loadFile(File data, int dirNum, int fileNum) throws RuntimeException {
        RandomAccessFile dataFile = null;
        try {
            if (!data.exists()) {
                try {
                    data.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException("Cannot create new file", e);
                }
            }
            dataFile = new RandomAccessFile(data, "rw");
            dataFile.seek(0);
            while (dataFile.getFilePointer() != dataFile.length()) {
                loadKeyAndValue(dataFile, dirNum, fileNum);
            }
        } catch (FileNotFoundException e2) {
            throw new RuntimeException("Cannot Load File4", e2);
        } catch (IOException e) {
            throw new RuntimeException("Cannot Load File6", e);
        } finally {
            try {
                if (dataFile != null) {
                    dataFile.close();
                }
            } catch (Throwable e1) {
                // not OK
            }
        }
    }

    protected void unloadMap() {
        RandomAccessFile[] filesArray = new RandomAccessFile[256];
        try {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    filesArray[i * 16 + j] = new RandomAccessFile(getFileWithNum(j, i), "rw");
                    filesArray[i * 16 + j].setLength(0);
                }
            }

            Set<Map.Entry<String, String>> entries = dataMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value != null) {
                    byte[] keyBytes = key.getBytes("UTF-8");
                    byte[] valueBytes = value.getBytes("UTF-8");
                    byte b = 0;
                    b = (byte) Math.abs(keyBytes[0]);
                    int ndirectory = b % 16;
                    int nfile = b / 16 % 16;
                    filesArray[ndirectory * 16 + nfile].writeInt(keyBytes.length);
                    filesArray[ndirectory * 16 + nfile].writeInt(valueBytes.length);
                    filesArray[ndirectory * 16 + nfile].write(keyBytes);
                    filesArray[ndirectory * 16 + nfile].write(valueBytes);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Cannot unload file correctly", e);
        } finally {
            try {
                for (int i = 0; i < 16; ++i) {
                    for (int j = 0; j < 16; ++j) {
                        if (filesArray[i * 16 + j].length() == 0) {
                            getFileWithNum(j, i).delete();
                        }
                        if (filesArray[i * 16 + j] != null) {
                            filesArray[i * 16 + j].close();
                        }
                    }
                }
            } catch (Throwable e) {
                // not OK
            }
        }
    }

    private void copyMap(HashMap<String, String> dest, HashMap<String, String> source) {
        dest.clear();
        Set<Map.Entry<String, String>> entries = source.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            dest.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String getName() {
        return currTable;
    }

    private void checkValue(String key, String name) {
        if (key == null) {
            throw new IllegalArgumentException(name + " is null");
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException(name + " is empty");
        }
        if (key.matches("[\\s]+")) {
            throw new IllegalArgumentException(name + " is empty");
        }
    }

    @Override
    public String get(String key) throws IllegalArgumentException {
        checkValue(key, "Key");
        return dataMap.get(key);
    }

    @Override
    public String put(String key, String value) throws IllegalArgumentException {
        checkValue(key, "Key");
        checkValue(value, "Value");
        String oldValue = dataMap.get(key);
        dataMap.put(key, value);
        return oldValue;
    }

    @Override
    public String remove(String key) throws IllegalArgumentException {
        checkValue(key, "Key");
        String val = dataMap.get(key);
        if (val != null) {
            dataMap.put(key, null);
        }
        return val;
    }

    @Override
    public int size() {
        int count = 0;
        Set<Map.Entry<String, String>> entries = dataMap.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            if (entry.getValue() != null) {
                ++count;
            }
        }
        return count;
    }

    @Override
    public int commit() {
        int changesCount = countChanges();
        unloadData();
        copyMap(oldDataMap, dataMap);
        return changesCount;
    }

    protected int countChanges() {
        int count = 0;
        Set<Map.Entry<String, String>> entries = dataMap.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();
            String oldValue = oldDataMap.get(key);
            if (value != oldValue || ((value != null) && (oldValue != null) && !(value.equals(oldValue)))) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int rollback() {
        int changesCount = countChanges();
        copyMap(dataMap, oldDataMap);
        return changesCount;
    }
}
