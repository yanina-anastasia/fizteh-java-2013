package ru.fizteh.fivt.students.annasavinova.filemap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;

public class DataBase implements Table {
    protected HashMap<String, Storeable> dataMap;
    protected HashMap<String, Storeable> oldDataMap;
    protected ArrayList<Class<?>> typesList;
    protected DataBaseProvider provider;

    private String currTable = "";
    private static String rootDir = "";
    private boolean hasLoadedData = false;

    public DataBase(String tableName, String root, TableProvider prov) {
        if (prov == null) {
            throw new IllegalArgumentException("Provider is null");
        }
        provider = (DataBaseProvider) prov;
        if (tableName == null) {
            throw new IllegalArgumentException("table name is null");
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
        dataMap = new HashMap<>();
        oldDataMap = new HashMap<>();
    }

    public void setHashMap(HashMap<String, Storeable> map) {
        copyMap(dataMap, map);
        copyMap(oldDataMap, map);
    }

    public void setTypes(List<Class<?>> columnTypes) {
        typesList = (ArrayList<Class<?>>) columnTypes;
    }

    public void setHasLoadedData(boolean property) {
        hasLoadedData = property;
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

    protected void unloadData() {
        if (hasLoadedData) {
            for (int i = 0; i < 16; ++i) {
                File currentDir = getDirWithNum(i);
                if (!currentDir.exists()) {
                    if (!currentDir.mkdir()) {
                        throw new RuntimeException("Cannot unload data: cannot create directory "
                                + currentDir.getAbsolutePath());
                    }
                }
                for (int j = 0; j < 16; ++j) {
                    File currentFile = getFileWithNum(j, i);
                    if (!currentFile.exists()) {
                        try {
                            if (!currentFile.createNewFile()) {
                                throw new RuntimeException("Cannot unload data: cannot create file "
                                        + currentFile.getAbsolutePath());
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Cannot unload data: cannot create file "
                                    + currentFile.getAbsolutePath());
                        }
                    }
                }
            }
            unloadMap();
            for (int i = 0; i < 16; ++i) {
                File currentDir = getDirWithNum(i);
                if (currentDir.list().length == 0) {
                    if (!currentDir.delete()) {
                        throw new RuntimeException("Cannot unload data: cannot delete directory "
                                + currentDir.getAbsolutePath());
                    }
                }
            }
            hasLoadedData = false;
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

            Set<Entry<String, Storeable>> entries = dataMap.entrySet();
            for (Map.Entry<String, Storeable> entry : entries) {
                String key = entry.getKey();
                Storeable value = entry.getValue();
                if (value != null) {
                    byte[] keyBytes = key.getBytes("UTF-8");
                    String str = provider.serialize(this, value);
                    byte[] valueBytes = str.getBytes("UTF-8");
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
                        if (filesArray[i * 16 + j] != null && filesArray[i * 16 + j].length() == 0) {
                            try {
                                filesArray[i * 16 + j].close();
                            } catch (Throwable e1) {
                                // not OK
                            }
                            getFileWithNum(j, i).delete();
                        }
                        if (filesArray[i * 16 + j] != null) {
                            try {
                                filesArray[i * 16 + j].close();
                            } catch (Throwable e1) {
                                // not OK
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                // not OK
            }
        }
    }

    private void copyMap(HashMap<String, Storeable> dest, HashMap<String, Storeable> source) {
        dest.clear();
        Set<Map.Entry<String, Storeable>> entries = source.entrySet();
        for (Map.Entry<String, Storeable> entry : entries) {
            dest.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String getName() {
        return currTable;
    }

    private void checkKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Key is empty");
        }
        if (key.contains(" ") || key.contains("\t") || key.contains(System.lineSeparator()) || key.split("//s").length > 1) {
            throw new IllegalArgumentException("Key contains whitespaces");
        }
    }

    @Override
    public Storeable get(String key) {
        if (provider.getTable(currTable) == null) {
            throw new IllegalStateException("table not exists");
        }
        checkKey(key);
        return dataMap.get(key);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (provider.getTable(currTable) == null) {
            throw new IllegalStateException("table not exists");
        }
        checkKey(key);
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        provider.checkColumns(this, value);
        Storeable oldValue = dataMap.get(key);
        dataMap.put(key, value);
        return oldValue;
    }

    @Override
    public Storeable remove(String key) throws IllegalArgumentException {
        if (provider.getTable(currTable) == null) {
            throw new IllegalStateException("table not exists");
        }
        checkKey(key);
        Storeable val = dataMap.get(key);
        if (val != null) {
            dataMap.put(key, null);
        }
        return val;
    }

    @Override
    public int size() {
        if (provider.getTable(currTable) == null) {
            throw new IllegalStateException("table not exists");
        }
        int count = 0;
        Set<Map.Entry<String, Storeable>> entries = dataMap.entrySet();
        for (Map.Entry<String, Storeable> entry : entries) {
            if (entry.getValue() != null) {
                ++count;
            }
        }
        return count;
    }

    @Override
    public int commit() throws IOException {
        if (provider.getTable(currTable) == null) {
            throw new IllegalStateException("table not exists");
        }
        int changesCount = countChanges();
        unloadData();
        copyMap(oldDataMap, dataMap);
        return changesCount;
    }

    private boolean compareTableRows(Table table, Storeable left, Storeable right) {
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (!left.getColumnAt(i).equals(right.getColumnAt(i))) {
                return false;
            }
        }
        return true;
    }

    protected int countChanges() {
        int count = 0;
        Set<Map.Entry<String, Storeable>> entries = dataMap.entrySet();
        for (Map.Entry<String, Storeable> entry : entries) {
            String key = entry.getKey();
            TableRow value = (TableRow) entry.getValue();
            TableRow oldValue = (TableRow) oldDataMap.get(key);
            if (value != oldValue
                    || ((value != null) && (oldValue != null) && !compareTableRows(this, value, oldValue))) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int rollback() {
        if (provider.getTable(currTable) == null) {
            throw new IllegalStateException("table not exists");
        }
        int changesCount = countChanges();
        copyMap(dataMap, oldDataMap);
        return changesCount;
    }

    @Override
    public int getColumnsCount() {
        return typesList.size();
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= typesList.size()) {
            throw new IndexOutOfBoundsException("Incorrect index");
        }
        return typesList.get(columnIndex);
    }
}
