package ru.fizteh.fivt.students.inaumov.filemap;

import ru.fizteh.fivt.students.inaumov.filemap.handlers.ReadHandler;
import ru.fizteh.fivt.students.inaumov.filemap.handlers.WriteHandler;

import java.io.IOException;
import java.io.File;

public class SingleFileTable extends AbstractTable {
    private static final String DEFAULT_DATABASE_FILE_NAME = "db.dat";

	public SingleFileTable(String dir, String tableName) throws IOException, IllegalArgumentException {
		super(dir, tableName);
	}

	public void loadTable() throws IOException {
		ReadHandler.loadFromFile(getDataBasePath(), tableHash);
	}

	public void saveTable() throws IOException {
		WriteHandler.saveToFile(getDataBasePath(), tableHash);
	}

    private String getDataBasePath() {
        return new File(getDir(), DEFAULT_DATABASE_FILE_NAME).getAbsolutePath();
    }
}
