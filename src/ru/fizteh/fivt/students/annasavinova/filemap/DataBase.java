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

    public DataBase(String tableName, String root) throws IllegalArgumentException {
        if (tableName == null) {
            IllegalArgumentException e = new IllegalArgumentException("table name is null");
            throw e;
        } else {
            currTable = tableName;
        }
        if (root.endsWith(File.separator)) {
            rootDir = root;
        } else {
            rootDir = root + File.separatorChar;
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
                        RuntimeException e = new RuntimeException("Cannot create new directory");
                        throw e;
                    }
                } else if (!currentDir.isDirectory()) {
                    RuntimeException e = new RuntimeException("Incorrect files in table");
                    throw e;
                }
                for (int j = 0; j < 16; ++j) {
                    File currentFile = getFileWithNum(j, i);
                    if (!currentFile.exists()) {
                        try {
                            currentFile.createNewFile();
                        } catch (IOException e) {
                            RuntimeException e1 = new RuntimeException("Cannot create new file");
                            throw e1;
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
                        RuntimeException e = new RuntimeException("Cannot unload data");
                        throw e;
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
                dataFile.close();
                RuntimeException e3 = new RuntimeException("Cannot Load File");
                throw e3;
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
                    RuntimeException e2 = new RuntimeException("Incorrect input");
                    throw e2;
                }
                String key = new String(keyBytes);
                String value = new String(valueBytes);
                dataMap.put(key, value);
                oldDataMap.put(key, value);
            }
        } catch (IOException | OutOfMemoryError e) {
            try {
                dataFile.close();
            } catch (IOException e1) {
                RuntimeException e3 = new RuntimeException("Cannot Load File");
                throw e3;
            }
            RuntimeException e3 = new RuntimeException("Cannot Load File");
            throw e3;
        }
    }

    protected void loadFile(File data, int dirNum, int fileNum) {
        RandomAccessFile dataFile = null;
        try {
            dataFile = new RandomAccessFile(data, "rw");
        } catch (FileNotFoundException e2) {
            RuntimeException e = new RuntimeException("Cannot Load File");
            throw e;
        }
        try {
            dataFile.seek(0);
            while (dataFile.getFilePointer() != dataFile.length()) {
                loadKeyAndValue(dataFile, dirNum, fileNum);
            }
        } catch (IOException e) {
            try {
                dataFile.close();
            } catch (IOException e1) {
                RuntimeException e3 = new RuntimeException("Cannot Load File");
                throw e3;
            }
            RuntimeException e3 = new RuntimeException("Cannot Load File");
            throw e3;
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
            copyMap(oldDataMap, dataMap);
            try {
                for (int i = 0; i < 16; ++i) {
                    for (int j = 0; j < 16; ++j) {
                        if (filesArray[i * 16 + j].length() == 0) {
                            getFileWithNum(j, i).delete();
                        }
                        filesArray[i * 16 + j].close();
                    }
                }
            } catch (IOException e) {
                RuntimeException e3 = new RuntimeException("Cannot unload file");
                throw e3;
            }
        } catch (IOException e) {
            RuntimeException e3 = new RuntimeException("Cannot unload file correctly");
            throw e3;
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
        return dataMap.size();
    }

    @Override
    public int commit() {
        int changesCount = countChanges();
        unloadData();
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
