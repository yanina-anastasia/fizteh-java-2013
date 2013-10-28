package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import java.io.File;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class MyTableProviderFactory implements TableProviderFactory {
	

	public MyTableProviderFactory() {}

	@Override
	public TableProvider create(String dir) throws IllegalArgumentException {
		if(dir == null) {
			throw new IllegalArgumentException("Directory can not be null.");
		}
		TableProvider newTableProvider = null;
		try {
			newTableProvider = new MyTableProvider(new File(dir));
		} catch (IllegalArgumentException e) {
			throw e;
		}
		return newTableProvider;
	}

}
