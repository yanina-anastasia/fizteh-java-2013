package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.msandrikova.filemap.DatabaseMap;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;
import ru.fizteh.fivt.students.msandrikova.storeable.StoreableTableProviderFactory;

public class State {
	public boolean isMultiFileHashMap;
	public boolean isStoreable;
	public ChangesCountingTable currentTable;
	public ChangesCountingTableProvider tableProvider;
	public ru.fizteh.fivt.storage.structured.Table currentStoreableTable;
	public ru.fizteh.fivt.storage.structured.TableProvider storeableTableProvider;
	
	public State(boolean isMultiHashFileMap, boolean isStoreable, String dir) {
		this.isMultiFileHashMap = isMultiHashFileMap;
		this.isStoreable = isStoreable;
		if(this.isMultiFileHashMap) {
			this.currentTable = null;
			ChangesCountingTableProviderFactory factory = new MyTableProviderFactory();
			try { 
				this.tableProvider = factory.create(dir);
			} catch (IllegalArgumentException e) {
				Utils.generateAnError(e.getMessage(), "state", false);
			}
		} else if(this.isStoreable) {
			 this.currentStoreableTable = null;
			 ru.fizteh.fivt.storage.structured.TableProviderFactory storeableFactory = new StoreableTableProviderFactory();
				try {
					this.storeableTableProvider = storeableFactory.create(dir);
				} catch (IllegalArgumentException | IOException e) {
					Utils.generateAnError(e.getMessage(), "state", false);
				}
			 
		} else {
			this.currentTable = new DatabaseMap(new File(dir), "db.dat");
			this.tableProvider = null;
		}
	}
	
	
	
	
}
