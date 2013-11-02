package ru.fizteh.fivt.students.elenav.states;

import java.io.File;
import java.io.PrintStream;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.elenav.multifilemap.MultiFileMapProvider;

public abstract class MonoMultiAbstractState extends FilesystemState implements Table {
	
	public MultiFileMapProvider provider = null;
	private MonoMultiAbstractState workingTable = null;
	
	protected MonoMultiAbstractState(String n, File wd, PrintStream s) {
		super(n, wd, s);
	}
	
	public MonoMultiAbstractState getWorkingTable() {
		return workingTable;
	}

	public void setWorkingTable(MonoMultiAbstractState workingTable) {
		this.workingTable = workingTable;
	}
	
}
