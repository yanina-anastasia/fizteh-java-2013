package ru.fizteh.fivt.students.demidov.multifilehashmap;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.junit.TableImplementation;
import ru.fizteh.fivt.students.demidov.junit.TableProviderImplementation;

public class MultiFileHashMapState extends BasicDataBaseState {
	public MultiFileHashMapState(TableProviderImplementation provider) {
		super(provider);
	}
	
	public void use(String tableName) throws IOException {
		TableImplementation gotTable = provider.getTable(tableName);
		if (gotTable == null) {
			throw new IOException(tableName + " doesn't exist");
		}
		usedTable = gotTable;	
	}
}
