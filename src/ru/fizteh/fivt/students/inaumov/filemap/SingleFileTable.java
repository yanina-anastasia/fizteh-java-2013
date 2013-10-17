package ru.fizteh.fivt.students.inaumov.filemap;

import java.io.IOException;

public class SingleFileTable extends AbstractTable {
	public SingleFileTable(String dir, String tableName) throws IOException, IncorrectArgumentsException {
		super(dir, tableName);
	}

	public void loadTable() throws IOException {
		loadFromFile(getDir());
	}

	public void saveTable() throws IOException {
		saveToFile(getDir());
	}
}
