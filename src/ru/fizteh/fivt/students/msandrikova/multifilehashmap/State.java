package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import java.io.File;

import ru.fizteh.fivt.students.msandrikova.filemap.DatabaseMap;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class State {
	public boolean isMultiFileHashMap;
	public ChangesCountingTable currentTable;
	public ChangesCountingTableProvider tableProvider;
	
	public State(boolean isMultiHashFileMap, String dir) {
		this.isMultiFileHashMap = isMultiHashFileMap;
		if(isMultiFileHashMap) {
			this.currentTable = null;
			ChangesCountingTableProviderFactory factory = new MyTableProviderFactory();
			try { 
				this.tableProvider = factory.create(dir);
			} catch (IllegalArgumentException e) {
				Utils.generateAnError(e.getMessage(), "state", false);
			}
		} else {
			this.currentTable = new DatabaseMap(new File(dir), "db.dat");
			this.tableProvider = null;
		}
	}
	
	
	
	
}
