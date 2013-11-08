package ru.fizteh.fivt.students.belousova.storable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.belousova.multifilehashmap.AbstractTable;
import ru.fizteh.fivt.students.belousova.utils.StorableUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StorableTable extends AbstractTable<String, Storeable> implements ChangesCountingTable {
    private List<Class<?>> columnTypes = new ArrayList<>();
    StorableTableProvider tableProvider = null;

    public StorableTable(File directory, StorableTableProvider tableProvider) throws IOException {
        dataDirectory = directory;
        this.tableProvider = tableProvider;
        File signatureFile = new File(directory, "signature.tsv");
        StorableUtils.readSignature(signatureFile, columnTypes);
        StorableUtils.readTable(directory, this, dataBase, tableProvider);
    }

    @Override
    public Storeable put(String key, Storeable value) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("empty key");
        }
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }
        if (value.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("empty value");
        }

//        try {
//            if (!StorableUtils.isStorableValid((StorableTableLine)value, columnTypes)) {
//                throw new IllegalArgumentException("alien storeable");
//            }
//        } catch (ClassCastException e) {
//            throw new IllegalArgumentException("alien storeable");
//        }

        if (key.matches(".*\\s+.*")) {
            throw new IllegalArgumentException("key with whitespaces");
        }

        return super.put(key, value);
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("empty key");
        }
        if (key.matches(".*\\s+.*")) {
            throw new IllegalArgumentException("key with whitespaces");
        }
        return super.remove(key);
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("empty key");
        }
        if (key.matches(".*\\s+.*")) {
            throw new IllegalArgumentException("key with whitespaces");
        }
        return super.get(key);
    }

    @Override
    public int commit() throws IOException {
        int counter = countChanges();
        for (String key : deletedKeys) {
            dataBase.remove(key);
        }
        dataBase.putAll(addedKeys);
        deletedKeys.clear();
        addedKeys.clear();
        StorableUtils.writeTable(dataDirectory, this, dataBase, tableProvider);
        return counter;
    }

    @Override
    public int getColumnsCount() {
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return columnTypes.get(columnIndex);
    }
}
