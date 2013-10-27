package ru.fizteh.fivt.students.visamsonov.storage;

import ru.fizteh.fivt.storage.strings.*;

public class TableFactory implements TableProviderFactory {

	public TableProvider create (String dir) {
		if (dir == null) {
			throw new IllegalArgumentException();
		}
		return new TableDirectory(dir);
	}
}