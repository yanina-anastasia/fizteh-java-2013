package ru.fizteh.fivt.students.eltyshev.multifilemap;

import ru.fizteh.fivt.students.eltyshev.filemap.base.AbstractTable;
import ru.fizteh.fivt.students.eltyshev.filemap.base.FilemapReader;
import ru.fizteh.fivt.students.eltyshev.filemap.base.FilemapWriter;
import ru.fizteh.fivt.students.eltyshev.filemap.base.SimpleTableBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MultifileTable extends AbstractTable {

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