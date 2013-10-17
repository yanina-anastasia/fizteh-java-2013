package ru.fizteh.fivt.students.inaumov.filemap;

public interface TableProvider {
	Table getTable(String name);
	
	Table createTable(String name);
	
	void removeTable(String name);
}
