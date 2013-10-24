package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class MyTableProvider implements TableProvider {
	private File currentDirectory;
	private Map<String, Table> mapOfTables = new HashMap<String, Table>();
	

	public MyTableProvider(File dir) throws IllegalArgumentException {
		this.currentDirectory = dir;
		if(!this.currentDirectory.exists()) {
			throw new IllegalArgumentException("Given directory does not exist.");
		} else if(!this.currentDirectory.isDirectory()) {
			throw new IllegalArgumentException("Given directory name does not correspond to directory.");
		}
	}

	@Override
	public Table getTable(String name) throws IllegalArgumentException {
		if(name == null) {
			throw new IllegalArgumentException("Table name can not be null");
		}
		if(this.mapOfTables.get(name) == null) {
			File tablePath = new File(this.currentDirectory, name);
			if(tablePath.exists()) {
				if(tablePath.isDirectory()) {
					Table newTable = new MyTable(this.currentDirectory, name);
					this.mapOfTables.put(name, newTable);
				} else {
					Utils.generateAnError("File with name \"" + name + "\" exists and is not directory.", "getTable", false);
				}
			}
		}
		return this.mapOfTables.get(name);
	}

	@Override
	public Table createTable(String name) throws IllegalArgumentException {
		if(name == null) {
			throw new IllegalArgumentException("Table name can not be null");
		}
		if(this.mapOfTables.get(name) != null){
			return null;
		}
		File tablePath = new File(this.currentDirectory, name);
		if(tablePath.exists()) {
			if(tablePath.isDirectory()) {
				Table newTable = new MyTable(this.currentDirectory, name);
				this.mapOfTables.put(name, newTable);
				return null;
			} else {
				Utils.generateAnError("File with name \"" + name + "\" exists and is not directory.", "getTable", false);
			}
		}
		Table newTable = new MyTable(this.currentDirectory, name);
		this.mapOfTables.put(name, newTable);
		return newTable;
	}

	@Override
	public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
		if(name == null) {
			throw new IllegalArgumentException("Table name can not be null");
		}
		File tablePath = new File(this.currentDirectory, name);
		if(this.mapOfTables.get(name) == null){
			if(tablePath.exists()) {
				if(tablePath.isDirectory()) {
					Table newTable = new MyTable(this.currentDirectory, name);
					this.mapOfTables.put(name, newTable);
				} else {
					Utils.generateAnError("File with name \"" + name + "\" exists and is not directory.", "getTable", false);
				}
			} else {
				throw new IllegalStateException();
			}
		}
		tablePath.delete();
		this.mapOfTables.remove(name);
	}

}
