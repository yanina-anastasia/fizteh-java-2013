package ru.fizteh.fivt.students.eltyshev.storable.database;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.eltyshev.multifilemap.DistributedLoader;
import ru.fizteh.fivt.students.eltyshev.multifilemap.DistributedSaver;
import ru.fizteh.fivt.students.eltyshev.storable.StoreableUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DatabaseTable implements Table {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private final String tableName;
    private final String databaseDirectory;
    private int size;
    private int uncommittedChangesCount;

    DatabaseTableProvider provider;

    private List<Class<?>> columnTypes;
    protected HashMap<String, Storeable> oldData;
    protected HashMap<String, Storeable> modifiedData;
    protected HashSet<String> deletedKeys;

    public DatabaseTable(DatabaseTableProvider provider, String databaseDirectory, String tableName, List<Class<?>> columnTypes) {
        if (columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("column types cannot be null");
        }

        this.tableName = tableName;
        this.databaseDirectory = databaseDirectory;
        this.columnTypes = columnTypes;
        this.provider = provider;

        oldData = new HashMap<String, Storeable>();
        modifiedData = new HashMap<String, Storeable>();
        deletedKeys = new HashSet<String>();
        uncommittedChangesCount = 0;


        try {
            checkTableDirectory();
            load();
        } catch (IOException e) {
            System.err.println("error loading table: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public Storeable get(String key) throws IllegalArgumentException {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (modifiedData.containsKey(key)) {
            return modifiedData.get(key);
        }
        if (deletedKeys.contains(key)) {
            return null;
        }

        return oldData.get(key);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            String message = key == null ? "key " : "value ";
            throw new IllegalArgumentException(message + "cannot be null");
        }

        key = key.trim();
        if (key.isEmpty()) {
            throw new IllegalArgumentException("key cannot be empty");
        }

        Storeable oldValue = getOldValueFor(key);
        modifiedData.put(key, value);
        if (oldValue == null) {
            size += 1;
        }
        uncommittedChangesCount += 1;
        return oldValue;
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Storeable oldValue = getOldValueFor(key);
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
        uncommittedChangesCount += 1;
        return oldValue;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int commit() throws IOException {
        int recordsCommitted = Math.abs(oldData.size() - size);
        for (final String keyToDelete : deletedKeys) {
            oldData.remove(keyToDelete);
        }
        for (final String keyToAdd : modifiedData.keySet()) {
            oldData.put(keyToAdd, modifiedData.get(keyToAdd));
        }
        deletedKeys.clear();
        modifiedData.clear();
        size = oldData.size();
        save();
        uncommittedChangesCount = 0;

        return recordsCommitted;
    }

    @Override
    public int rollback() {
        int recordsDeleted = Math.abs(oldData.size() - size);
        deletedKeys.clear();
        modifiedData.clear();
        size = oldData.size();
        uncommittedChangesCount = 0;

        return recordsDeleted;
    }

    @Override
    public int getColumnsCount() {
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex > getColumnsCount()) {
            throw new IndexOutOfBoundsException();
        }
        return columnTypes.get(columnIndex);
    }

    public int getUncommittedChangesCount() {
        return uncommittedChangesCount;
    }

    public String getDatabaseDirectory() {
        return databaseDirectory;
    }

    private void load() throws IOException {
        DistributedLoader.load(new StoreableTableBuilder(provider, this));
    }

    private void save() throws IOException {
        DistributedSaver.save(new StoreableTableBuilder(provider, this));
    }

    private Storeable getOldValueFor(String key) {
        Storeable oldValue = modifiedData.get(key);
        if (oldValue == null && !deletedKeys.contains(key)) {
            oldValue = oldData.get(key);
        }
        return oldValue;
    }

    private void checkTableDirectory() throws IOException {
        File tableDirectory = new File(getDatabaseDirectory(), getName());
        if (!tableDirectory.exists()) {
            tableDirectory.mkdir();
            writeSignatureFile();
        }
    }

    private void writeSignatureFile() throws IOException {
        File tableDirectory = new File(getDatabaseDirectory(), getName());
        File signatureFile = new File(tableDirectory, DatabaseTableProvider.SIGNATURE_FILE);
        signatureFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(signatureFile));
        List<String> formattedColumnTypes = StoreableUtils.formatColumnTypes(columnTypes);
        String signature = StoreableUtils.join(formattedColumnTypes);
        writer.write(signature);
        writer.close();
    }
}
