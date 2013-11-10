package ru.fizteh.fivt.students.dmitryKonturov.dataBase.databaseImplementation;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.DatabaseException;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.utils.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableImplementation implements Table {
    private final String tableName;
    private final TableProviderImplementation tableProvider;
    private final List<Class<?>> columnTypes;
    private final int columnsCount;
    private HashMap<String, Storeable> currentChangesMap;
    private HashMap<String, Storeable> savedMap;
    private final Path tablePath;

    /**
     * loads database from its folder
     */
    TableImplementation(String tableName, TableProviderImplementation tableProvider) throws IOException,
            DatabaseException {

        this.tableName = tableName;
        this.tableProvider = tableProvider;
        this.tablePath = tableProvider.getWorkspace().resolve(tableName);
        Path signatureFile = tablePath.resolve(StoreableUtils.getSignatureFileName());
        this.columnTypes = StoreableUtils.loadSignatureFile(signatureFile);
        this.columnsCount = columnTypes.size();
        this.currentChangesMap = new HashMap<>();
        this.savedMap = new HashMap<>();
        Map<String, String> tmpBase = new HashMap<>();
        MultiFileMapLoaderWriter.loadDatabase(tableProvider.getWorkspace(), tableName, tmpBase);
        for (Map.Entry<String, String> entry : tmpBase.entrySet()) {
            try {
                Storeable storeable = tableProvider.deserialize(this, entry.getValue());
                savedMap.put(entry.getKey(), storeable);
            } catch (Exception e) {
                throw new DatabaseException("Cannot deserialize file", e);
            }

        }
    }

    /**
     * not even try to load database
     */
    TableImplementation(String tableName, TableProviderImplementation tableProvider, List<Class<?>> columnTypes) throws
            IOException, RuntimeException, DatabaseException {

        this.tableName = tableName;
        this.tableProvider = tableProvider;
        this.columnTypes = new ArrayList<>();
        for (Class<?> type : columnTypes) {
            if (type == null) {
                throw new IllegalArgumentException("type can't be null");
            }
            if (!StoreableUtils.isSupportedType(type)) {
                throw new IllegalArgumentException("type no supported");
            }
            this.columnTypes.add(type);
        }
        this.columnsCount = columnTypes.size();
        this.currentChangesMap = new HashMap<>();
        this.savedMap = new HashMap<>();
        this.tablePath = tableProvider.getWorkspace().resolve(tableName);
        Files.createDirectory(tablePath);
        StoreableUtils.writeSignatureFile(tablePath.resolve(StoreableUtils.getSignatureFileName()), this.columnTypes);
    }

    private boolean isTableStoreableEqual(Storeable first, Storeable second) {
        if (first == null || second == null) {
            throw new NullPointerException();
        }
        boolean result = true;
        for (int i = 0; i < columnsCount; ++i) {
            if (!first.getColumnAt(i).equals(second.getColumnAt(i))) {
                result = false;
            }
        }
        return result;
    }

    public int getUnsavedChangesCount() {
        int changesNum = 0;
        for (Map.Entry<String, Storeable> entry : currentChangesMap.entrySet()) {
            String key = entry.getKey();
            Storeable value = entry.getValue();
            Storeable savedValue = savedMap.get(key);
            if (value == null) {
                if (savedValue != null) {
                    ++changesNum;
                }
            } else {
                if (savedValue == null) {
                    ++changesNum;
                } else if (!isTableStoreableEqual(value, savedValue)) { // must be true
                    ++changesNum;
                }
            }

        }
        return changesNum;
    }

    @Override
    public String getName() {
        if (tableProvider.getTable(tableName) != this) {
            throw new IllegalStateException("Table was removed");
        }
        return tableName;
    }

    @Override
    public Storeable get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Empty key");
        }
        if (tableProvider.getTable(tableName) != this) {
            throw new IllegalStateException("Table was removed");
        }
        if (currentChangesMap.containsKey(key)) {
            return currentChangesMap.get(key);
        } else {
            return savedMap.get(key);
        }
    }

    @Override
    public Storeable put(String key, Storeable value) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Empty key");
        }
        if (key.contains(" ")) {
            throw new IllegalArgumentException("Key shouldn't contain whitespaces");
        }
        if (value == null) {
            throw new IllegalArgumentException("Empty value");
        }
        if (tableProvider.getTable(tableName) != this) {
            throw new IllegalStateException("Table was removed");
        }

        StoreableUtils.checkStoreableBelongsToTable(this, value);

        Storeable toReturn = get(key);
        if (isTableStoreableEqual(value, savedMap.get(key))) {  // savedValue not changes
            currentChangesMap.remove(key);
        } else {
            currentChangesMap.put(key, value);
        }
        return toReturn;
    }

    @Override
    public Storeable remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Empty key");
        }
        if (tableProvider.getTable(tableName) != this) {
            throw new IllegalStateException("Table was removed");
        }
        Storeable toReturn = get(key);
        currentChangesMap.put(key, null);
        return toReturn;
    }

    @Override
    public int size() {
        if (tableProvider.getTable(tableName) != this) {
            throw new IllegalStateException("Table was removed");
        }
        int tableSize = savedMap.size();
        for (Map.Entry<String, Storeable> entry : currentChangesMap.entrySet()) {
            String key = entry.getKey();
            Storeable value = entry.getValue();
            Storeable savedValue = savedMap.get(key);
            if (savedValue == null) { // Was not saved
                if (value != null) {
                    ++tableSize;
                }
            } else {                  // saved
                if (value == null) {
                    --tableSize;
                }
            }
        }
        return tableSize;
    }

    @Override
    public int commit() throws IOException {
        if (tableProvider.getTable(tableName) != this) {
            throw new IllegalStateException("Table was removed");
        }
        int changesNumber = 0;
        for (Map.Entry<String, Storeable> entry : currentChangesMap.entrySet()) {
            String key = entry.getKey();
            Storeable value = entry.getValue();
            Storeable savedValue;
            if (value == null) { // need to remove value
                savedValue = savedMap.remove(key);
                if (savedValue != null) { //if contains before
                    ++changesNumber;
                }
            } else {
                savedValue = savedMap.put(key, value);
                if (savedValue == null) { // new key mapping
                    ++changesNumber;
                } else {                  // only if different values
                    if (!isTableStoreableEqual(savedValue, value)) { //must be false
                        ++changesNumber;
                    }
                }
            }
        }
        Map<String, String> savedStringMap = new HashMap<>();
        for (Map.Entry<String, Storeable> entry : savedMap.entrySet()) {
            try {
                savedStringMap.put(entry.getKey(), tableProvider.serialize(this, entry.getValue()));
            } catch (Exception e) {
                throw new IOException("Cannot serialize storable");
            }
        }
        try {
            MultiFileMapLoaderWriter.writeDatabase(tableProvider.getWorkspace(), tableName, savedStringMap);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
        return changesNumber;
    }

    @Override
    public int rollback() {
        if (tableProvider.getTable(tableName) != this) {
            throw new IllegalStateException("Table was removed");
        }

        int toReturn = getUnsavedChangesCount();
        currentChangesMap.clear();
        return toReturn;
    }

    @Override
    public int getColumnsCount() {
        if (tableProvider.getTable(tableName) != this) {
            throw new IllegalStateException("Table was removed");
        }
        return columnsCount;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (tableProvider.getTable(tableName) != this) {
            throw new IllegalStateException("Table was removed");
        }
        if (columnIndex < 0) {
            throw new IndexOutOfBoundsException("Negative index");
        }
        if (columnIndex >= columnsCount) {
            throw new IndexOutOfBoundsException("Index bigger than columns count");
        }
        return columnTypes.get(columnIndex);
    }
}
