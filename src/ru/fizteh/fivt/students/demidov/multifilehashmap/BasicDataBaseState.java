package ru.fizteh.fivt.students.demidov.multifilehashmap;

import java.io.IOException;

import ru.fizteh.fivt.students.demidov.filemap.BasicState;
import ru.fizteh.fivt.students.demidov.junit.TableImplementation;
import ru.fizteh.fivt.students.demidov.junit.TableProviderImplementation;

public abstract class BasicDataBaseState implements BasicState {
	public BasicDataBaseState(TableProviderImplementation provider) {
		this.provider = provider;
		usedTable = null;
	}
	
	public TableImplementation getUsedTable() throws IOException {
		if (usedTable == null) {
			throw new IOException("no table");
		}
		return usedTable;
	}
	
	public void drop(String tableName) throws IOException {
		if ((usedTable != null) && (usedTable.getName().equals(tableName))) {
			usedTable = null;
		}
		try {
			provider.removeTable(tableName);
		} catch(IllegalArgumentException catchedException) {
			throw new IOException(catchedException);
		} catch(IllegalStateException catchedException) {
			throw new IOException(catchedException);
		}
	}
	
	public void create(String tableName) throws IOException {
		TableImplementation createdTable = null;
		try {
			createdTable = provider.createTable(tableName);		
		} catch(IllegalArgumentException catchedException) {
			throw new IOException(catchedException);
		} catch(IllegalStateException catchedException) {
			throw new IOException(catchedException);
		}
		if (createdTable == null) {
			throw new IOException(tableName + " exists");
		}
	}
	
	public abstract void use(String tableName) throws IOException;
	
	protected TableProviderImplementation provider;	
	protected TableImplementation usedTable;
}