package ru.fizteh.fivt.students.eltyshev.storable.database;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.eltyshev.filemap.base.TableBuilder;

import java.io.File;
import java.text.ParseException;
import java.util.Set;

public class StoreableTableBuilder implements TableBuilder {
    DatabaseTableProvider provider;
    DatabaseTable table;

    public StoreableTableBuilder(DatabaseTableProvider provider, DatabaseTable table) {
        this.provider = provider;
        this.table = table;
    }

    @Override
    public String get(String key) {
        Storeable value = table.get(key);
        try {
            String representation = provider.serialize(table, value);
            return representation;
        } catch (ColumnFormatException e) {
            return null;
        }
    }

    @Override
    public void put(String key, String value) {
        Storeable objectValue = null;
        try {
            objectValue = provider.deserialize(table, value);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
        table.put(key, objectValue);
    }

    @Override
    public Set<String> getKeys() {
        return table.oldData.keySet();
    }

    @Override
    public File getTableDirectory() {
        return new File(table.getDatabaseDirectory(), table.getName());
    }
}
