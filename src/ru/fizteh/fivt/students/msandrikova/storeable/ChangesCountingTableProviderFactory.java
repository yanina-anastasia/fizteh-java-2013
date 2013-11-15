package ru.fizteh.fivt.students.msandrikova.storeable;

import java.io.IOException;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public interface ChangesCountingTableProviderFactory extends TableProviderFactory {
	public ChangesCountingTableProvider create(String dir) throws IllegalArgumentException, IOException;

}
