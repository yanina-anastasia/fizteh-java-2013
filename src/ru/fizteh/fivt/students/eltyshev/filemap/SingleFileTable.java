package ru.fizteh.fivt.students.eltyshev.filemap;

import ru.fizteh.fivt.students.eltyshev.filemap.base.AbstractTable;
import ru.fizteh.fivt.students.eltyshev.filemap.base.FilemapReader;
import ru.fizteh.fivt.students.eltyshev.filemap.base.FilemapWriter;
import ru.fizteh.fivt.students.eltyshev.filemap.base.SimpleTableBuilder;

import java.io.*;

public class SingleFileTable extends AbstractTable {

    private static final String DATABASE_FILE_NAME = "db.dat";

    public SingleFileTable(String directory, String tableName) {
        super(directory, tableName);
    }

    protected void load() throws IOException {
        FilemapReader.loadFromFile(getDatabaseFilePath(), new SimpleTableBuilder(this));
    }

    protected void save() throws IOException {
        FilemapWriter.saveToFile(getDatabaseFilePath(), oldData.keySet(), new SimpleTableBuilder(this));
    }

    private String getDatabaseFilePath() {
        File databaseFile = new File(getDirectory(), DATABASE_FILE_NAME);
        return databaseFile.getAbsolutePath();
    }
}
