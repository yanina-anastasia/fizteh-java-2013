package ru.fizteh.fivt.students.elenav.states;

import java.io.File;
import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.filemap.FileMapState;

public abstract class MonoMultiAbstractState extends FilesystemState {

	private FileMapState workingTable = null;
	
	protected MonoMultiAbstractState(String n, File wd, PrintStream s) {
		super(n, wd, s);
	}
	
	public FileMapState getWorkingTable() {
		return workingTable;
	}

	public void setWorkingTable(FileMapState t) {
		workingTable = t;
	}
	
}
