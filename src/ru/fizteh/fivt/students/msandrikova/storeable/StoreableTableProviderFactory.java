package ru.fizteh.fivt.students.msandrikova.storeable;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class StoreableTableProviderFactory implements TableProviderFactory {

	@Override
	public TableProvider create(String dir) throws IllegalArgumentException, IOException {
		if(Utils.isEmpty(dir)) {
			throw new IllegalArgumentException("Directory can not be null.");
		}
		TableProvider newTableProvider = null;
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
