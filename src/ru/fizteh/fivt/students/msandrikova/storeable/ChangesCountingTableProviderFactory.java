package ru.fizteh.fivt.students.msandrikova.storeable;

import java.io.IOException;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public interface ChangesCountingTableProviderFactory extends TableProviderFactory {
	
	@Override
    ChangesCountingTableProvider create(String path) throws IOException;
}
