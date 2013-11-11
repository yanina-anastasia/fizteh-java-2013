package ru.fizteh.fivt.students.msandrikova.storeable;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.msandrikova.storeable.ChangesCountingTableProvider;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class StoreableTableProviderFactory implements ChangesCountingTableProviderFactory {

	@Override
	public ChangesCountingTableProvider create(String dir) throws IllegalArgumentException, IOException {
		if(Utils.isEmpty(dir)) {
			throw new IllegalArgumentException("Directory can not be null.");
		}
		ChangesCountingTableProvider newTableProvider = null;
		try {
			newTableProvider = new StoreableTableProvider(new File(dir));
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		return newTableProvider;
	}

}
