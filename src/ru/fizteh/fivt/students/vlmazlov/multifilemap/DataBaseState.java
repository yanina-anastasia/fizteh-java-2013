package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class DataBaseState {
	private DiffCountingTable activeTable;
	private final DiffCountingTableProvider provider;

	protected DataBaseState() {
		provider = null;
	}

	public DataBaseState(DiffCountingTableProvider provider) {
		if (provider == null) {
			throw new IllegalArgumentException();
		}

		this.provider = provider;
	}

	public DiffCountingTable getActiveTable() {
		return activeTable;
	}

	public void setActiveTable(DiffCountingTable newActiveTable) {
		activeTable = newActiveTable;
	}

	public DiffCountingTableProvider getProvider() {
		return provider;
	}
}