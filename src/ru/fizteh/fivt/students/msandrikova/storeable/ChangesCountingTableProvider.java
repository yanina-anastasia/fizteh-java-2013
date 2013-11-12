package ru.fizteh.fivt.students.msandrikova.storeable;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public interface ChangesCountingTableProvider extends TableProvider {
	
	@Override
	ChangesCountingTable getTable(String name);
	
	@Override
	ChangesCountingTable createTable(String name, List<Class<?>> columnTypes) throws IOException;
	

}
