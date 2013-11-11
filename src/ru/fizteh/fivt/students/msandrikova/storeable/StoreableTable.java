package ru.fizteh.fivt.students.msandrikova.storeable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

public class StoreableTable implements ChangesCountingTable {
	private boolean isDeleted;
	private String name;
	private File tablePath;
	private List<Class<?>> columnTypes = new ArrayList<Class<?>>();
	private Map<Integer, TableRow> mapOfStoreables = new HashMap<Integer, TableRow>();
	private int MAX_DIRECTORIES_AMOUNT = 16;
	private int MAX_TABLE_SIZE = 1000*1000*100;
	
	public StoreableTable(File dir, String name, List<Class<?>> columnTypes) throws IOException {
		this.name = name;
		this.columnTypes = columnTypes;
		this.tablePath = new File(dir, name);
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Storeable get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Storeable put(String key, Storeable value)
			throws ColumnFormatException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Storeable remove(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int commit() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int rollback() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getColumnsCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Class<?> getColumnType(int columnIndex)
			throws IndexOutOfBoundsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getChangesCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDeleted() {
		this.isDeleted = true;	
	}

}
