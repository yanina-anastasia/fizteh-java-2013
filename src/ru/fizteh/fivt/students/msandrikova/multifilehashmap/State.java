package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.msandrikova.filemap.DBMap;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class State {
	private boolean isMultiFileHashMap;
	private boolean isFileMap;
	private TableProvider currentTableProvider;
	private DBMap currentDBMap;
	private Table currentTable;
	
	public State() {
		this.isMultiFileHashMap = false;
		this.isFileMap = false;
		this.currentDBMap = null;
		this.currentTableProvider = null;
		this.currentTable = null;
	}
	
	public void setIsMultiFileHashMap(boolean isMultiFileHashMap) {
		this.isMultiFileHashMap = isMultiFileHashMap;
	}
	
	public void setIsFileMap(boolean isFileMap) {
		this.isFileMap = isFileMap;
	}
	
	public void setTableProvider(TableProvider currentTableProvider) {
		this.currentTableProvider = currentTableProvider;
	}
	
	
	public void setDBMap(File currentDirectory) {
		try {
			this.currentDBMap = new DBMap(currentDirectory, "db.dat");
		} catch (FileNotFoundException e) {
			Utils.generateAnError("Fatal error during reading.", "DBMap", false);
		} catch (IOException e) {
			Utils.generateAnError("Fatal error during reading.", "DBMap", false);
		}
	}
	
	public Table setCurrentTable(Table currentTable) {
		Table oldTable = null;
		if(this.currentTable != null) {
			oldTable = this.currentTable;
		}
		this.currentTable = currentTable;
		return oldTable;
	}
	
	public boolean getIsMultiFileHashMap() {
		return this.isMultiFileHashMap;
	}
	
	public boolean getIsFileMap() {
		return this.isFileMap;
	}
	
	public TableProvider getTableProvider() {
		return this.currentTableProvider;
	}
	
	public DBMap getDBMap() {
		return this.currentDBMap;
	}
	
	public Table getCurrentTable() {
		return this.currentTable;
	}
	
}
