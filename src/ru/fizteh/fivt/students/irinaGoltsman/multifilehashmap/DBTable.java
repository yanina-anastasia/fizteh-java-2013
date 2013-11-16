package ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DBTable implements Table {

    private File tableDirectory;
    private HashMap<String, Storeable> originalTable = new HashMap<>();
    private List<Class<?>> columnTypes;
    private TableProvider tableProvider;
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private ThreadLocal<HashMap<String, Storeable>> tableOfChanges = new ThreadLocal<HashMap<String, Storeable>>() {
        @Override
        protected HashMap<String, Storeable> initialValue() {
            return new HashMap<>();
        }
    };
    private ThreadLocal<Set<String>> removedKeys = new ThreadLocal<Set<String>>() {
        @Override
        protected HashSet<String> initialValue() {
            return new HashSet<>();
        }
    };

    public DBTable(File inputTableDirectory, TableProvider provider) throws IOException {
        FileManager.checkTableDir(inputTableDirectory);
        tableDirectory = inputTableDirectory;
        tableProvider = provider;
        columnTypes = FileManager.readTableSignature(tableDirectory);
        HashMap<String, String> tmpTable = new HashMap<>();
        FileManager.readDBFromDisk(tableDirectory, tmpTable);
        List<String> keys = new ArrayList<>(tmpTable.keySet());
        List<String> values = new ArrayList<>(tmpTable.values());
        for (int i = 0; i < values.size(); i++) {
            try {
                Storeable rowValue = tableProvider.deserialize(this, values.get(i));
                try {
                    readLock.lock();
                    originalTable.put(keys.get(i), rowValue);
                } finally {
                    readLock.unlock();
                }
            } catch (ParseException e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public String getName() {
        return tableDirectory.getName();
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("remove: key is null");
        }
        Storeable value = tableOfChanges.get().get(key);
        if (value == null) {
            if (removedKeys.get().contains(key)) {
                return null;
            }
            try {
                readLock.lock();
                value = originalTable.get(key);
            } finally {
                readLock.unlock();
            }
        }
        return value;
    }

    //Проверяет соответствие типов в переданном Storeable с типами таблицы
    private void checkEqualityTypes(Storeable storeable) throws ColumnFormatException {
        for (int numberOfType = 0; numberOfType < columnTypes.size(); numberOfType++) {
            Object type;
            try {
                type = storeable.getColumnAt(numberOfType);
            } catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException("table put: types of storeable mismatch");
            }
            if (type != null) {
                if (!columnTypes.get(numberOfType).equals(type.getClass())) {
                    throw new ColumnFormatException("table put: types of storeable mismatch");
                }
            }
        }
        try {  //Проверка на то, что число колонок в storeable не больше допустимого
            storeable.getColumnAt(columnTypes.size());
        } catch (IndexOutOfBoundsException e) {
            return;
        }
        throw new ColumnFormatException("storeable has more columns then must have");
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (value == null || key == null) {
            throw new IllegalArgumentException("put: key or value is null");
        }
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("put: key is empty");
        }
        if (key.matches(".*\\s+.*")) {
            throw new IllegalArgumentException("put: key contains white space");
        }
        Storeable newValue = value;
        checkEqualityTypes(newValue);
        Storeable originalValue = null;
        try {
            readLock.lock();
            originalValue = originalTable.get(key);
        } finally {
            readLock.unlock();
        }
        Storeable oldValue = tableOfChanges.get().put(key, newValue);
        //Значит здесь впервые происходит перезаписывание старого значения.
        if (!removedKeys.get().contains(key) && oldValue == null) {
            oldValue = originalValue;
        }
        if (originalValue != null) {
            removedKeys.get().add(key);
        }
        return oldValue;
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("table remove: key is null");
        }
        Storeable value = tableOfChanges.get().get(key);
        if (value == null) {
            if (!removedKeys.get().contains(key)) {
                try {
                    readLock.lock();
                    value = originalTable.get(key);
                } finally {
                    readLock.unlock();
                }
                if (value != null) {
                    removedKeys.get().add(key);
                }
            }
        } else {
            tableOfChanges.get().remove(key);
            try {
                readLock.lock();
                if (originalTable.containsKey(key)) {
                    removedKeys.get().add(key);
                }
            } finally {
                readLock.unlock();
            }
        }
        return value;
    }

    @Override
    public int size() {
        return tableOfChanges.get().size() + originalTable.size() - removedKeys.get().size();
    }

    //@return Количество сохранённых ключей.
    @Override
    public int commit() throws IOException {
        int count = countTheNumberOfChanges();
        try {
            writeLock.lock();
            for (String delString : removedKeys.get()) {
                originalTable.remove(delString);
            }
            originalTable.putAll(tableOfChanges.get());
            List<String> keys = new ArrayList<>(originalTable.keySet());
            List<Storeable> values = new ArrayList<>(originalTable.values());
            HashMap<String, String> serializedTable = new HashMap();
            for (int i = 0; i < values.size(); i++) {
                String serializedValue = tableProvider.serialize(this, values.get(i));
                serializedTable.put(keys.get(i), serializedValue);
            }
            FileManager.writeTableOnDisk(tableDirectory, serializedTable);
        } finally {
            writeLock.unlock();
        }
        tableOfChanges.get().clear();
        removedKeys.get().clear();
        return count;
    }

    @Override
    public int rollback() {
        int count = countTheNumberOfChanges();
        tableOfChanges.get().clear();
        removedKeys.get().clear();
        return count;
    }

    @Override
    public int getColumnsCount() {
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex >= columnTypes.size() || columnIndex < 0) {
            throw new IndexOutOfBoundsException("invalid column index: " + columnIndex);
        }
        return columnTypes.get(columnIndex);
    }

    public int countTheNumberOfChanges() {
        int countOfChanges = 0;
        for (String currentKey : removedKeys.get()) {
            if (tableOfChanges.get().containsKey(currentKey)) {
                Storeable currentValue = tableOfChanges.get().get(currentKey);
                try {
                    readLock.lock();
                    if (checkStoreableForEquality(originalTable.get(currentKey), currentValue)) {
                        continue;
                    }
                } finally {
                    readLock.unlock();
                }
            }
            countOfChanges++;
        }
        for (String currentKey : tableOfChanges.get().keySet()) {
            try {
                readLock.lock();
                if (!originalTable.containsKey(currentKey)) {
                    countOfChanges++;
                }
            } finally {
                readLock.unlock();
            }
        }
        return countOfChanges;
    }

    private boolean checkStoreableForEquality(Storeable first, Storeable second) {
        String string1 = tableProvider.serialize(this, first);
        String string2 = tableProvider.serialize(this, second);
        return string1.equals(string2);
    }
}
