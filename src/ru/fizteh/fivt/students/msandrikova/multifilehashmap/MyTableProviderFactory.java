package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import java.io.File;

public class MyTableProviderFactory implements ChangesCountingTableProviderFactory {
	@Override
	public ChangesCountingTableProvider create(String dir) throws IllegalArgumentException {
		if(dir == null) {
			throw new IllegalArgumentException("Directory can not be null.");
		}
		ChangesCountingTableProvider newTableProvider = null;
		try {
			newTableProvider = new MyTableProvider(new File(dir));
		} catch (IllegalArgumentException e) {
			throw e;
		}
		return newTableProvider;
	}

}
