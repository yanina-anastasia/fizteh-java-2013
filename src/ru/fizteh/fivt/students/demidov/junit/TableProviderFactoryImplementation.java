package ru.fizteh.fivt.students.demidov.junit;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class TableProviderFactoryImplementation implements TableProviderFactory {
	public TableProviderFactoryImplementation() {}
	
	public TableProviderImplementation create(String dir) {
		if (dir == null) {
			throw new IllegalArgumentException("wrong basic directory: " + dir);
		}		
		return new TableProviderImplementation(dir);
	}
}
