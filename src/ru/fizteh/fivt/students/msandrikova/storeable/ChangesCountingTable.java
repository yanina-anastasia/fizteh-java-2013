package ru.fizteh.fivt.students.msandrikova.storeable;

import java.util.List;

import ru.fizteh.fivt.storage.structured.Table;

public interface ChangesCountingTable extends Table {
	int getChangesCount();
	
	void setDeleted();
	
	List<Class<?>> getColumnTypes();
}
