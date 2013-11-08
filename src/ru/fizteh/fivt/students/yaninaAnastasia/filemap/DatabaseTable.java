package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;

public class DatabaseTable implements Table {
    public HashMap<String, Storeable> oldData;
    public HashMap<String, Storeable> modifiedData;
    public HashSet<String> deletedKeys;
    public int size;
    public int uncommittedChanges;
    private String tableName;
    public List<Class<?>> columnTypes;
    DatabaseTableProvider provider;


    public DatabaseTable(String name, List<Class<?>> colTypes, DatabaseTableProvider providerRef) {
        this.tableName = name;
        oldData = new HashMap<String, Storeable>();
        modifiedData = new HashMap<String, Storeable>();
        deletedKeys = new HashSet<String>();
        columnTypes = colTypes;
        provider = providerRef;
        uncommittedChanges = 0;
        for (final Class<?> columnType : columnTypes) {
            if (columnType == null || formatColumnType(columnType) == null) {
                throw new IllegalArgumentException("unknown column type");
            }
        }
    }

    public static int getDirectoryNum(String key) {
        int keyByte = Math.abs(key.getBytes(StandardCharsets.UTF_8)[0]);
        return keyByte % 16;
    }

    public static int getFileNum(String key) {
        int keyByte = Math.abs(key.getBytes(StandardCharsets.UTF_8)[0]);
        return (keyByte / 16) % 16;
    }

    public String getName() {
        if (tableName == null) {
            throw new IllegalArgumentException("Table name cannot be null");
        }
        return tableName;
    }

    public void putName(String name) {
        this.tableName = name;
    }

    public Storeable get(String key) throws IllegalArgumentException {
        if (key == null || (key.isEmpty() || key.trim().isEmpty())) {
            throw new IllegalArgumentException("Table name cannot be null");
        }
        if (modifiedData.containsKey(key)) {
            return modifiedData.get(key);
        }
        if (deletedKeys.contains(key)) {
            return null;
        }
        return oldData.get(key);
    }

    public static boolean checkStringCorrect(String string) {
        return string.matches("\\s*") || string.split("\\s+").length != 1;
    }

    private boolean tryToGetUnnecessaryColumn(Storeable value) {
        try {
            value.getColumnAt(columnTypes.size());
        } catch (IndexOutOfBoundsException e) {
            return true;
        }
        return false;
    }

    public Storeable put(String key, Storeable value) throws IllegalArgumentException {
        if ((key == null) || (key.trim().isEmpty())) {
            throw new IllegalArgumentException("Key can not be null");
        }
        if (key.matches("\\s*") || key.split("\\s+").length != 1) {
            throw new IllegalArgumentException("Key contains whitespaces");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        if (!checkAlienStoreable(value)) {
            //System.out.println(value);
            throw new ColumnFormatException("Alien storeable");
        }
        if (!tryToGetUnnecessaryColumn(value)) {                                  //
            throw new ColumnFormatException("Incorrect value to put.");           //
        } /*                                                                        //
        for (int index = 0; index < getColumnsCount(); ++index) {
            try {
                switch (formatColumnType(columnTypes.get(index))) {
                    case "String":
                        String stringValue = (String) value.getColumnAt(index);
                        if (checkStringCorrect(stringValue)) {
                            throw new ParseException("value cannot be null", 0);
                        }
                        break;
                }
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }    */
        /*
        for (int i = 0; i < getColumnsCount(); i++) {
            try {
                if (value.getColumnAt(i) == null) {
                    continue;
                }
                switch (formatColumnType(columnTypes.get(i))) {
                    case "String":
                        String stringValue = (String) value.getColumnAt(i);
                        if (stringValue.trim().isEmpty()) {
                            throw new ParseException("Value cannot be empty", 0);
                        }
                        break;
                }
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }  */

        //Fixes 40s tests

        Storeable oldValue = null;
        oldValue = modifiedData.get(key);
        if (oldValue == null && !deletedKeys.contains(key)) {
            oldValue = oldData.get(key);
        }
        modifiedData.put(key, value);
        if (deletedKeys.contains(key)) {
            deletedKeys.remove(key);
        }
        if (oldValue == null) {
            size += 1;
        }
        uncommittedChanges += 1;
        return oldValue;
    }

    public Storeable remove(String key) throws IllegalArgumentException {
        if (key == null || (key.isEmpty() || key.trim().isEmpty())) {
            throw new IllegalArgumentException("Key name cannot be null");
        }
        Storeable oldValue = null;
        oldValue = modifiedData.get(key);
        if (oldValue == null && !deletedKeys.contains(key)) {
            oldValue = oldData.get(key);
        }
        if (modifiedData.containsKey(key)) {
            modifiedData.remove(key);
            if (oldData.containsKey(key)) {
                deletedKeys.add(key);
            }
        } else {
            deletedKeys.add(key);
        }
        if (oldValue != null) {
            size -= 1;
        }
        uncommittedChanges += 1;
        return oldValue;
    }

    public int size() {
        return size;
    }

    public int commit() {
        int recordsCommited = changesCount();
        for (String keyToDelete : deletedKeys) {
            oldData.remove(keyToDelete);
        }
        for (String keyToAdd : modifiedData.keySet()) {
            if (modifiedData.get(keyToAdd) != null) {
                oldData.put(keyToAdd, modifiedData.get(keyToAdd));
            }
        }
        deletedKeys.clear();
        modifiedData.clear();
        size = oldData.size();
        TableBuilder tableBuilder = new TableBuilder(provider, this);
        save(tableBuilder);
        uncommittedChanges = 0;

        return recordsCommited;
    }

    public int rollback() {
        int recordsDeleted = changesCount();

        deletedKeys.clear();
        modifiedData.clear();
        size = oldData.size();

        uncommittedChanges = 0;

        return recordsDeleted;
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= getColumnsCount()) {
            throw new IndexOutOfBoundsException();
        }
        return columnTypes.get(columnIndex);
    }

    public Storeable storeableGet(String key) {
        return oldData.get(key);
    }

    public void storeablePut(String key, Storeable value) {
        oldData.put(key, value);
    }

    public boolean save(TableBuilder tableBuilder) {
        if (oldData == null) {
            return true;
        }
        if (tableName.equals("")) {
            return true;
        }
        File tablePath = new File(System.getProperty("fizteh.db.dir"), tableName);
        for (int i = 0; i < 16; i++) {
            String directoryName = String.format("%d.dir", i);
            File path = new File(tablePath, directoryName);
            boolean isDirEmpty = true;
            ArrayList<HashSet<String>> keys = new ArrayList<HashSet<String>>(16);
            for (int j = 0; j < 16; j++) {
                keys.add(new HashSet<String>());
            }
            for (String step : oldData.keySet()) {
                int nDirectory = getDirectoryNum(step);
                if (nDirectory == i) {
                    int nFile = getFileNum(step);
                    keys.get(nFile).add(step);
                    isDirEmpty = false;
                }
            }

            if (isDirEmpty) {
                try {
                    if (path.exists()) {
                        DatabaseTableProvider.recRemove(path);
                    }
                } catch (IOException e) {
                    return false;
                }
                continue;
            }
            if (path.exists()) {
                File file = path;
                try {
                    if (!DatabaseTableProvider.recRemove(file)) {
                        System.err.println("File was not deleted");
                        return false;
                    }
                } catch (IOException e) {
                    return false;
                }
            }
            if (!path.mkdir()) {
                return false;
            }
            for (int j = 0; j < 16; j++) {
                File filePath = new File(path, String.format("%d.dat", j));
                try {
                    saveTable(keys.get(j), filePath.toString(), tableBuilder);
                } catch (IOException e) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean saveTable(Set<String> keys, String path, TableBuilder tableBuilder) throws IOException {
        if (keys.isEmpty()) {
            try {
                Files.delete(Paths.get(path));
            } catch (IOException e) {
                return false;
            }
            return false;
        }
        RandomAccessFile temp = new RandomAccessFile(path, "rw");
        try {
            long offset = 0;
            temp.setLength(0);
            for (String step : keys) {
                offset += step.getBytes(StandardCharsets.UTF_8).length + 5;
            }
            for (String step : keys) {
                byte[] bytesToWrite = step.getBytes(StandardCharsets.UTF_8);
                temp.write(bytesToWrite);
                temp.writeByte(0);
                temp.writeInt((int) offset);
                String myOffset = tableBuilder.get(step);
                offset += myOffset.getBytes(StandardCharsets.UTF_8).length;
            }
            for (String key : keys) {
                String value = tableBuilder.get(key);
                temp.write(value.getBytes(StandardCharsets.UTF_8));
            }
            temp.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private int changesCount() {
        HashSet<String> tempSet = new HashSet<>();
        HashSet<String> toRemove = new HashSet<>();
        tempSet.addAll(modifiedData.keySet());
        tempSet.addAll(deletedKeys);
        for (String key : tempSet) {
            if (modifiedData.containsKey(key) && compare(oldData.get(key), modifiedData.get(key))) {
                toRemove.add(key);
            }
        }
        return tempSet.size() - toRemove.size();
    }

    private boolean compare(Storeable key1, Storeable key2) {
        if (key1 == null && key2 == null) {
            return true;
        }
        if (key1 == null || key2 == null) {
            return false;
        }
        return key1.equals(key2);
    }

    public int getColumnsCount() {
        return columnTypes.size();
    }

    public static String formatColumnType(Class<?> columnType) {
        switch (columnType.getName()) {
            case "java.lang.Integer":
                return "int";
            case "java.lang.Long":
                return "long";
            case "java.lang.Byte":
                return "byte";
            case "java.lang.Float":
                return "float";
            case "java.lang.Double":
                return "double";
            case "java.lang.Boolean":
                return "boolean";
            case "java.lang.String":
                return "String";
            default:
                return null;
        }
    }

    public boolean checkAlienStoreable(Storeable storeable) {
        for (int index = 0; index < getColumnsCount(); ++index) {
            try {
                Object o = storeable.getColumnAt(index);
                if (o == null) {
                    continue;
                }
                if (!o.getClass().equals(getColumnType(index))) {
                    System.out.println(String.format("expected: %s; actual: %s", getColumnType(index).getName(), o.getClass().getName()));
                    System.out.println(storeable);
                    return false;
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.println(String.format("index: %s; size: %d", e.getMessage(), getColumnsCount()));
                System.out.println(storeable);
                return false;
            }
        }
        boolean flag = true;
        try {
            storeable.getColumnAt(getColumnsCount());
            flag = false;
        } catch (IndexOutOfBoundsException e) {
            //
        }
        return flag;
    }
}
