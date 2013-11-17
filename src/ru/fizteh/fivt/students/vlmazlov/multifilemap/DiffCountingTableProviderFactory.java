package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public interface DiffCountingTableProviderFactory extends TableProviderFactory {
	@Override
	DiffCountingTableProvider create(String dir);

}