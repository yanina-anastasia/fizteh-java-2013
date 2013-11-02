package ru.fizteh.fivt.students.elenav.multifilemap;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.elenav.filemap.FileMapState;
import ru.fizteh.fivt.students.elenav.shell.ShellState;

public class MultiFileMapProvider implements TableProvider {

	private File workingDirectory = null;
	private PrintStream stream;
	private ShellState shell;
	
	public MultiFileMapProvider(File db, PrintStream s) {
		if (db == null) {
			throw new IllegalArgumentException("can't create provider: null name");
		}
		if (!db.isDirectory()) {
			throw new IllegalArgumentException("can't create provider: name is file or name doesn't exist");
		}
		setWorkingDirectory(db);
		setStream(s);
		shell = new ShellState("TableProvider'sShell", db, s);
	}


	@Override
	public Table getTable(String name) {
		if (name == null) {
			throw new IllegalArgumentException("can't get table with null name");
		}
		File f = new File(getWorkingDirectory(), name);
		if (!f.isDirectory()) {
			return null;
		}
		return new FileMapState(name, f, getStream());
	}
	
	@Override
	public Table createTable(String name) {
		if (name == null) {
			throw new IllegalArgumentException("can't create table with null name");
		}
		File f = new File(getWorkingDirectory(), name);
		if (f.exists()) {
			return null;
		}
		f.mkdir();
		return new FileMapState(name, f, getStream());
	}
	
	@Override
	public void removeTable(String name) {
		if (name == null) {
            throw new IllegalArgumentException("can't remove table: invalid name");
        }
		File table = new File(getWorkingDirectory(), name);
		if (!table.exists()) {
			throw new IllegalStateException("can't remove table: table not exist");
		}
		try {
			shell.rm(name);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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

	public ShellState getShell() {
		return shell;
	}

	public void setShell(ShellState shell) {
		this.shell = shell;
	}

}
