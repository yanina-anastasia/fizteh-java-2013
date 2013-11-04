package ru.fizteh.fivt.students.belousova.storable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.belousova.multifilehashmap.AbstractTableProvider;
import ru.fizteh.fivt.students.belousova.utils.StorableUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class StorableTableProvider extends AbstractTableProvider<ChangesCountingTable>
        implements ChangesCountingTableProvider {

    public StorableTableProvider(File directory) throws IOException {
        if (directory == null) {
            throw new IllegalArgumentException("null directory");
        }
        if (!directory.exists()) {
            directory.mkdir();
        } else if (!directory.isDirectory()) {
            throw new IllegalArgumentException("'" + directory.getName() + "' is not a directory");
        }

        dataDitectory = directory;

        if (!directory.canRead()) {
            throw new IOException("directory is unavailable");
        }
        for (File tableFile : directory.listFiles()) {
            tableMap.put(tableFile.getName(), new StorableTable(tableFile, this));
        }

    }

    @Override
    public ChangesCountingTable createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("empty name");
        }
        if (!name.matches(TABLE_NAME_FORMAT)) {
            throw new IllegalArgumentException("incorrect name");
        }

        if (tableMap.containsKey(name)) {
            return null;
        }
        if (columnTypes == null) {
            throw new IllegalArgumentException("ColumnTypes list is not set");
        }
        if (columnTypes.isEmpty()) {
            throw new IllegalArgumentException("ColumnTypes list is empty");
        }
        File tableFile = new File(dataDitectory, name);
        tableFile.mkdir();
        try {
            StorableUtils.writeSignature(tableFile, columnTypes);
        } catch (IOException e) {
            throw new IllegalArgumentException("wrong column type table");
        }
        ChangesCountingTable table = new StorableTable(tableFile, this);
        tableMap.put(name, table);
        return table;
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        try {
            List<Class<?>> columnTypes = new ArrayList<>();
            for (int i = 0; i < table.getColumnsCount(); i++) {
                columnTypes.add(table.getColumnType(i));
            }
            return StorableUtils.readStorableValue(value, columnTypes);
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("wrong data format", 0);
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        List<Class<?>> columnTypes = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            columnTypes.add(table.getColumnType(i));
        }
        return StorableUtils.writeStorableToString(value, columnTypes);
    }

    @Override
    public Storeable createFor(Table table) {
        List<Class<?>> columnTypes = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            columnTypes.add(table.getColumnType(i));
        }
        return new StorableTableLine(columnTypes);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        List<Class<?>> columnTypes = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            columnTypes.add(table.getColumnType(i));
        }
        Storeable storeable = new StorableTableLine(columnTypes);
        int columnIndex = 0;
        for (Object value : values) {
            storeable.setColumnAt(columnIndex, value);
        }
        return storeable;
    }
}
