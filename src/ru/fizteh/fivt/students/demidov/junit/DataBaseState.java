package ru.fizteh.fivt.students.demidov.junit;

import java.io.IOException;

import ru.fizteh.fivt.students.demidov.multifilehashmap.BasicDataBaseState;

public class DataBaseState extends BasicDataBaseState {
	public DataBaseState(TableProviderImplementation provider) {
		super(provider);
	}
	
	public void use(String tableName) throws IOException {
		if (usedTable != null) {
			int changesNumber = usedTable.getChangesNumber();
			if (usedTable.getChangesNumber() > 0) {
				throw new IOException(changesNumber + " unsaved changes");
			}
		}
		TableImplementation gotTable = null;
		try {
			gotTable = provider.getTable(tableName);
		} catch(IllegalArgumentException catchedException) {
			throw new IOException(catchedException);
		}
		if (gotTable == null) {
			throw new IOException(tableName + " doesn't exist");
		}
		usedTable = gotTable;	
	}
}
