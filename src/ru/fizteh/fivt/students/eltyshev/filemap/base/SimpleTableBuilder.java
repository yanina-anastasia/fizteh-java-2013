package ru.fizteh.fivt.students.eltyshev.filemap.base;

import java.io.File;
import java.util.Set;

public class SimpleTableBuilder implements TableBuilder {
    AbstractTable table;

    public SimpleTableBuilder(AbstractTable table) {
        this.table = table;
    }

    @Override
    public String get(String key) {
        return table.rawGet(key);
    }

    @Override
    public void put(String key, String value) {
        table.rawPut(key, value);
    }

    @Override
    public Set<String> getKeys() {
        return table.oldData.keySet();
    }

    @Override
    public File getTableDirectory() {
        return new File(table.getDirectory(), table.getName());
    }
}
