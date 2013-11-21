package ru.fizteh.fivt.students.eltyshev.multifilemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.eltyshev.filemap.base.AbstractStorage;
import ru.fizteh.fivt.students.eltyshev.filemap.base.SimpleTableBuilder;
import ru.fizteh.fivt.students.eltyshev.filemap.base.StringTable;

import java.io.File;
import java.io.IOException;

public class MultifileTable extends StringTable {

    public MultifileTable(String directory, String tableName) {
        super(directory, tableName);
    }

    protected void save() throws IOException {
        DistributedSaver.save(new SimpleTableBuilder(this));
    }

    protected void load() throws IOException {
        DistributedLoader.load(new SimpleTableBuilder(this));
    }

    private File getTableDirectory() {
        File tableDirectory = new File(getDirectory(), getName());
        if (!tableDirectory.exists()) {
            tableDirectory.mkdir();
        }
        return tableDirectory;
    }
}