package ru.fizteh.fivt.students.eltyshev.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.eltyshev.filemap.base.*;

import java.io.*;

public class SingleFileTable extends StringTable {

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
