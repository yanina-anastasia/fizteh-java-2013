package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.storage.strings.TableProvider;

public interface DiffCountingTableProvider extends TableProvider {
	@Override
	DiffCountingTable getTable(String name);

	@Override
	DiffCountingTable createTable(String name);

}