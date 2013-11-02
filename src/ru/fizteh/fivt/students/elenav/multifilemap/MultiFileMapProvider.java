package ru.fizteh.fivt.students.elenav.multifilemap;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.elenav.utils.Functions;

public class MultiFileMapProvider implements TableProvider {

	private final String NAME_FORMAT = "[a-zA-Zà-ßÀ-ß0-9]+";
	
	private File workingDirectory = null;
	private PrintStream stream;
	public HashMap<String, MultiFileMapState> tables = new HashMap<>();
	
	public MultiFileMapProvider(File db, PrintStream s) {
		if (db == null) {
			throw new IllegalArgumentException("can't create provider: null name");
		}
		if (!db.isDirectory()) {
			throw new IllegalArgumentException("can't create provider: name is file or name doesn't exist");
		}
		setWorkingDirectory(db);
		setStream(s);
	}


	@Override
	public Table getTable(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("can't get table with null name");
		}
		if (!name.matches(NAME_FORMAT)) {
			throw new RuntimeException("can't get table with invalid name");
		}
		return tables.get(name);
	}
	
	@Override
	public MultiFileMapState createTable(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("can't create table with null name");
		}
		if (!name.matches(NAME_FORMAT)) {
			throw new RuntimeException("can't create table with invalid name");
		}
		File f = new File(getWorkingDirectory(), name);
		if (f.exists()) {
			return null;
		}
		f.mkdir();
		MultiFileMapState table = new MultiFileMapState(name, f, getStream());
		tables.put(name, table);
		return table;
	}
	
	@Override
	public void removeTable(String name) {
		if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("can't remove table: invalid name");
        }
		if (tables.get(name) == null) {
			throw new IllegalArgumentException("can't remove table: table not exist");
		}
		if (!name.matches(NAME_FORMAT)) {
			throw new RuntimeException("can't remove table with invalid name");
		}
		try {
			Functions.deleteRecursively(tables.get(name).getWorkingDirectory());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		tables.remove(name);
	}

	public File getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public PrintStream getStream() {
		return stream;
	}

	public void setStream(PrintStream stream) {
		this.stream = stream;
	}

}
