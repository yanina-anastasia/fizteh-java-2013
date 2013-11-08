package ru.fizteh.fivt.students.asaitgalin.storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.container.TableContainer;
import ru.fizteh.fivt.students.asaitgalin.storable.extensions.ExtendedTable;
import ru.fizteh.fivt.students.asaitgalin.storable.values.TableValuePackerStorable;
import ru.fizteh.fivt.students.asaitgalin.storable.values.TableValueUnpackerStorable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MultiFileTable implements ExtendedTable {
    // Underlying container
    private TableContainer<Storeable> container;

    private String name;
    private File tableDir;
    private TableProvider provider;
    private List<Class<?>> columnTypes;

    public MultiFileTable(File tableDir, String name, TableProvider provider) {
        this.name = name;
        this.tableDir = tableDir;
        this.provider = provider;
        MultiFileTableSignatureWorker worker = new MultiFileTableSignatureWorker(tableDir);
        columnTypes = worker.readColumnTypes();
        this.container = new TableContainer<>(tableDir, new TableValuePackerStorable(this, provider),
                new TableValueUnpackerStorable(this, provider));
    }

    public MultiFileTable(File tableDir, String name, TableProvider provider, List<Class<?>> columnTypes) {
        this.name = name;
        this.tableDir = tableDir;
        this.provider = provider;
        this.columnTypes = columnTypes;
        this.container = new TableContainer<>(tableDir, new TableValuePackerStorable(this, provider),
                new TableValueUnpackerStorable(this, provider));
        MultiFileTableSignatureWorker worker = new MultiFileTableSignatureWorker(tableDir);
        worker.writeColumnTypes(columnTypes);
    }

    @Override
    public int getChangesCount() {
        return container.containerGetChangesCount();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("get: key is null");
        }
        return container.containerGetValue(key);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("put: key or value is null");
        }
        if (key.matches("\\s*") || key.split("\\s+").length != 1) {
            throw new IllegalArgumentException("put: key or value is empty");
        }
        //checkValue(value);
        return container.containerPutValue(key, value);
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("remove: key is null");
        }
        return container.containerRemoveValue(key);
    }

    @Override
    public int size() {
        return container.containerGetSize();
    }

    @Override
    public int commit() throws IOException {
        return container.containerCommit();
    }

    @Override
    public int rollback() {
        return container.containerRollback();
    }

    @Override
    public int getColumnsCount() {
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= columnTypes.size()) {
            throw new IndexOutOfBoundsException(String.format("table, getColumnType: index %d out of bounds", columnIndex));
        }
        return columnTypes.get(columnIndex);
    }

    public void load() throws IOException {
        container.containerLoad();
    }

    private void checkValue(Storeable st) {
        int counter = 0;
        try {
            for (; counter < columnTypes.size(); ++counter) {
                Object o = st.getColumnAt(counter);
                if (o != null) {
                    if (!columnTypes.get(counter).equals(o.getClass())) {
                        throw new IllegalArgumentException("table, put: alien storable");
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            //
        }
        if (counter != columnTypes.size()) {
            throw new IllegalArgumentException("table, put: alien storable");
        }
    }
}
