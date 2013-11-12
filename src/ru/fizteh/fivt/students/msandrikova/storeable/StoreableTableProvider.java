package ru.fizteh.fivt.students.msandrikova.storeable;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class StoreableTableProvider implements TableProvider {
	private File currentDirectory;
	private Map<String, Table> mapOfTables = new HashMap<String, Table>(); 
	

	public StoreableTableProvider(File dir) throws IllegalArgumentException, IOException {
		this.currentDirectory = dir;
		if(!this.currentDirectory.exists()) {
			if(!dir.mkdir()) {
				throw new IOException("Table provider: Can not create working directory.");
			}
		} else if(!this.currentDirectory.isDirectory()) {
			throw new IllegalArgumentException("Given directory name does not correspond to directory.");
		} else {
			for(File f : dir.listFiles()) {
				if(f.isDirectory()){
					Table newTable = null;
					try {
						List<Class<?>> columnTypes = Utils.getClassTypes(f);
						newTable = new StoreableTable(this.currentDirectory, f.getName(), columnTypes, this);
					} catch (IOException e) {
						throw e;
					}
					this.mapOfTables.put(f.getName(), newTable);
				}
			}
		}
	}

	@Override
	public void removeTable(String name) throws IOException, IllegalStateException, IllegalArgumentException {
		if(Utils.isEmpty(name) || !Utils.testBadSymbols(name)) {
			throw new IllegalArgumentException("Table name can not be null or empty");
		}
		File tablePath = new File(this.currentDirectory, name);
		if(tablePath.exists() && !tablePath.isDirectory()) {
			throw new IllegalArgumentException("File with name '" + name + "' should be directory.");
		}
		
		if(this.mapOfTables.get(name) == null) {
			throw new IllegalStateException();
		}
		
		try {
			Utils.remover(tablePath, "drop", false);
		} catch (IOException e) {
			throw e;
		}
		this.mapOfTables.remove(name);
	}

	@Override
	public Storeable deserialize(Table table, String value) throws ParseException {
		Storeable row = this.createFor(table);
		JSONArray valueJSON = null;
		try {
			valueJSON = new JSONArray(value);
		} catch (JSONException e) {
			throw new ParseException(e.getMessage(), 0);
		}
		if(valueJSON.length() != table.getColumnsCount()) {
			throw new ParseException("Incorrect column count, expected " + table.getColumnsCount() + ".", 0);
		}
		Object o = null;
		for(int i = 0; i < table.getColumnsCount(); ++i) {
			try {
				o = valueJSON.get(i);
			} catch (JSONException e) {
				o = null;
			}
			if(table.getColumnType(i).equals(Byte.class) && o.getClass().equals(Integer.class)) {
				o = (Byte) o;
			}
			if(table.getColumnType(i).equals(Float.class) && o.getClass().equals(Double.class)) {
				o = (Float) o;
			}
			try {
				row.setColumnAt(i, o);
			} catch (ColumnFormatException e) {
				throw new ParseException(e.getMessage(), 0);
			}
		}
		return row;
	}


	@Override
	public String serialize(Table table, Storeable value) throws ColumnFormatException {
		JSONArray valueJSON = new JSONArray();
		Object o = null;
		for(int i = 0; i < table.getColumnsCount(); ++i) {
			o = value.getColumnAt(i);
			if(!o.getClass().equals(table.getColumnType(i))) {
				throw new ColumnFormatException("Incorrect column type.");
			}
			if(table.getColumnType(i).equals(Byte.class)) {
				o = (Integer) o;
			}
			if(table.getColumnType(i).equals(Float.class)) {
				o = (Double) o;
			}
			valueJSON.put(o);
		}
		return valueJSON.toString();
	}

	@Override
	public Storeable createFor(Table table) {
		List<Class<?>> columnTypes = new ArrayList<Class<?>>();
		for(int i = 0; i < table.getColumnsCount(); ++i) {
			columnTypes.add(table.getColumnType(i));
		}
		return new TableRow(columnTypes);
	}

	@Override
	public Storeable createFor(Table table, List<?> values) throws IndexOutOfBoundsException {
		Storeable row = this.createFor(table);
		for(int i = 0; i < table.getColumnsCount(); ++i) {
			row.setColumnAt(i, values.get(i));
		}
		return row;
	}

	@Override
	public Table getTable(String name) throws IllegalArgumentException {
		if(Utils.isEmpty(name) || !Utils.testBadSymbols(name)) {
			throw new IllegalArgumentException("Table name can not be null or empty or contain bad symbols");
		}
		File tablePath = new File(this.currentDirectory, name);
		if(tablePath.exists() && !tablePath.isDirectory()) {
			throw new IllegalArgumentException("File with name '" + name + "' should be directory.");
		}
		return this.mapOfTables.get(name);
	}

	@Override
	public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
		if(Utils.isEmpty(name) || !Utils.testBadSymbols(name)) {
			throw new IllegalArgumentException("Table name can not be null or empty or contain bad symbols");
		}
		if(this.mapOfTables.get(name) != null) {
			return null;
		}
		Table newTable = null;
		try {
			newTable = new StoreableTable(this.currentDirectory, name, columnTypes, this);
		} catch (IOException e) {
			throw e;
		}
		this.mapOfTables.put(name, newTable);
		return newTable;
	}

}
