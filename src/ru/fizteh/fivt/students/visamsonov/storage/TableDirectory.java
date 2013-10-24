package ru.fizteh.fivt.students.visamsonov.storage;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.Table;
import java.io.*;

public class TableDirectory implements TableProvider {

	private final String dbDirectory;

	public TableDirectory (String dbDirectory) {
		this.dbDirectory = dbDirectory;
	}

	public Table getTable (String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		File table = new File(dbDirectory, name);
		if (!table.isDirectory()) {
			return null;
		}
		try {
			return new MultiFileStorage(table.getAbsolutePath(), name);
		}
		catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public Table createTable (String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		File table = new File(dbDirectory, name);
		if (table.isDirectory()) {
			throw new IllegalArgumentException(name + " exists");
		}
		if (!table.mkdir()) {
			throw new IllegalArgumentException();
		}
		String tablePath = table.getAbsolutePath();
		try {
			return new MultiFileStorage(table.getAbsolutePath(), name);
		}
		catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private boolean delete (String parent, String name) {
		if (name == null) {
			return true;
		}
		try {
			File file = new File(parent, name);
			String[] content = file.list();
			if (content != null) {
				for (int i = 0; i < content.length; i++) {
					if (!delete(file.getCanonicalPath(), content[i])) {
						return false;
					}
				}
			}
			return file.delete();
		}
		catch (IOException e) {}
		return false;
	}

	public void removeTable (String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		File table = new File(dbDirectory, name);
		if (!table.isDirectory()) {
			throw new IllegalStateException(name + " not exists");
		}
		if (!delete(dbDirectory, name)) {
			throw new IllegalArgumentException();
		}
	}
};