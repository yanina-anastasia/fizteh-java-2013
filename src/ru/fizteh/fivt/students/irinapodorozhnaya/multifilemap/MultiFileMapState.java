package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import ru.fizteh.fivt.students.irinapodorozhnaya.db.DbState;

public class MultiFileMapState extends DbState {
	
	private Table workingTable;
	
	public MultiFileMapState(InputStream in, PrintStream out) throws IOException {
		super(in, out);
		String path = System.getProperty("fizteh.db.dir");
		if (path == null) {
			throw new IOException("can't get property");
		}
		setCurrentDir(new File(path));
		add(new CommandUse(this));
		add(new CommandCreate(this));
		add(new CommandDrop(this));
	}

	@Override
	protected void open() throws IOException {		
	}
	
	@Override
	public String getValue(String key) throws IOException {
		if (workingTable == null) {
			throw new IOException("no table");
		}
		return workingTable.get(key);
	}

	@Override
	public String removeValue(String key) throws IOException {
		if (workingTable == null) {
			throw new IOException("no table");
		}
		return workingTable.remove(key);
	}

	@Override
	public String put(String key, String value) throws IOException {
		if (workingTable == null) {
			throw new IOException("no table");
		}
		return workingTable.put(key, value);
	}
	
	@Override
	public void commitDif() throws IOException {
		if (workingTable != null) {
			workingTable.commitDif();
		}
	}

	public void setWorkingTable(Table workingTable) {
		this.workingTable = workingTable;
	}
}
