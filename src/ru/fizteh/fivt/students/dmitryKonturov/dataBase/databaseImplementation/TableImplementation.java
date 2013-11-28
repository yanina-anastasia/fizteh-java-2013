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
            return first == null && second == null;
        }
        boolean result = true;
        for (int i = 0; i < columnsCount; ++i) {
            Object firstObject = first.getColumnAt(i);
            Object secondObject = second.getColumnAt(i);
            if (firstObject == null) {
                result &= (secondObject == null);
            } else {
                result &= secondObject != null && firstObject.equals(secondObject);
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

    private void checkTableState() {
        if (!tableProvider.isProviderLoading() && tableProvider.getTable(tableName) != this) {
            throw new IllegalStateException("Table was removed");
        }
    }

    private void checkKey(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("key must have positive length");
        }
        if (key.matches("(.*\\s+.*)+")) {
            throw new IllegalArgumentException("key contains space characters");
        }
    }

    @Override
    public String getName() {
        checkTableState();
        return tableName;
    }

    @Override
    public Storeable get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Empty key");
        }
        checkTableState();
        if (currentChangesMap.containsKey(key)) {
            return currentChangesMap.get(key);
        } else {
            return savedMap.get(key);
        }
    }

    @Override
    public Storeable put(String key, Storeable value) throws IllegalArgumentException {
        checkKey(key);
        if (value == null) {
            throw new IllegalArgumentException("Empty value");
        }

        checkTableState();

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
        checkTableState();
        Storeable toReturn = get(key);
        currentChangesMap.put(key, null);
        return toReturn;
    }

    @Override
    public int size() {
        checkTableState();
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
        checkTableState();
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
        checkTableState();

        int toReturn = getUnsavedChangesCount();
        currentChangesMap.clear();
        return toReturn;
    }

    @Override
    public int getColumnsCount() {
        checkTableState();
        return columnsCount;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkTableState();
        if (columnIndex < 0) {
            throw new IndexOutOfBoundsException("Negative index");
        }
        if (columnIndex >= columnsCount) {
            throw new IndexOutOfBoundsException("Index bigger than columns count");
        }
        return columnTypes.get(columnIndex);
    }
}
