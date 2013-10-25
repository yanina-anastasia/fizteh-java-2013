package ru.fizteh.fivt.students.inaumov.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import java.io.IOException;
import java.io.File;

public class SingleFileTable extends AbstractTable {
    private static final String DEFAULT_DATABASE_FILE_NAME = "db.dat";

	public SingleFileTable(String dir, String tableName) throws IOException, IllegalArgumentException {
		super(dir, tableName);
	}

	public void loadTable() throws IOException {
<<<<<<< HEAD
		ReadHandler.loadFromFile(getDataBasePath(), tableHash);
	}

	public void saveTable() throws IOException {
		WriteHandler.saveToFile(getDataBasePath(), tableHash);
=======
		loadFromFile(getDataBasePath());
	}

	public void saveTable() throws IOException {
		saveToFile(getDataBasePath());
>>>>>>> 52e46dc6916f1d8fa0aff1b37a2cd587ef33ceb3
	}

    private String getDataBasePath() {
        return new File(getDir(), DEFAULT_DATABASE_FILE_NAME).getAbsolutePath();
    }
}
