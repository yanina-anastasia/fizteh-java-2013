package ru.fizteh.fivt.students.vlmazlov.filemap;

import ru.fizteh.fivt.storage.strings.Table;

public interface DataBaseState extends Table {
	public boolean tableExists();
}