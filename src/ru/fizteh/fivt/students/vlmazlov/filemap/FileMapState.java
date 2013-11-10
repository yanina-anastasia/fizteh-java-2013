package ru.fizteh.fivt.students.vlmazlov.filemap;

import ru.fizteh.fivt.students	.vlmazlov.multifilemap.DiffCountingTableProvider;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.DiffCountingTable;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.DataBaseState;

public class FileMapState extends DataBaseState {
	private DiffCountingTable table;

	public FileMapState(DiffCountingTable table) {
		super();
		this.table = table;
	}

	public DiffCountingTable getActiveTable() {
		return table;
	}

	public void setActiveTable(DiffCountingTable newActiveTable) {
		table = newActiveTable;
	}

	//not to be called
	public DiffCountingTableProvider getProvider() {
		return null;
	}
}