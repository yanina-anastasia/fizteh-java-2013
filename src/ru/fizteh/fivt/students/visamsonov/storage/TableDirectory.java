package ru.fizteh.fivt.students.visamsonov.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.io.*;
import java.text.ParseException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;

public class TableDirectory implements TableProviderInterface {

	private final String dbDirectory;
	private final HashMap<String, StructuredTableInterface> tables = new HashMap<String, StructuredTableInterface>();
	private static final String VALID_TABLENAME_REGEXP = "[A-Za-zА-Яа-я0-9\\._-]+";
	private static Map<String, Class<?>> allowedTypes = new HashMap();
	static {
		allowedTypes.put("int", Integer.class);
		allowedTypes.put("long", Long.class);
		allowedTypes.put("float", Float.class);
		allowedTypes.put("byte", Byte.class);
		allowedTypes.put("double", Double.class);
		allowedTypes.put("boolean", Boolean.class);
		allowedTypes.put("String", String.class);
	}

	public TableDirectory (String dbDirectory) {
		this.dbDirectory = dbDirectory;
	}

	static public Class<?> getTypeByName (String name) {
		return allowedTypes.get(name);
	}

	static public String getNameByType (Class<?> type) {
		for (Map.Entry<String, Class<?>> entry : allowedTypes.entrySet()) {
			if (entry.getValue().isAssignableFrom(type)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public StructuredTableInterface getTable (String name) {
		if (name == null || !name.matches(VALID_TABLENAME_REGEXP)) {
			throw new IllegalArgumentException();
		}
		File table = new File(dbDirectory, name);
		if (!table.isDirectory()) {
			return null;
		}
		try {
			StructuredTableInterface savedTable = tables.get(name);
			if (savedTable == null) {
				savedTable = new StructuredStorage(table.getCanonicalPath(), name, null, this);
				tables.put(name, savedTable);
			}
			return savedTable;
		}
		catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public StructuredTableInterface createTable (String name, List<Class<?>> columnTypes) throws IOException {
		if (name == null || columnTypes == null || columnTypes.size() == 0 || !name.matches(VALID_TABLENAME_REGEXP)) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < columnTypes.size(); i++) {
			if (!allowedTypes.containsValue(columnTypes.get(i))) {
				throw new IllegalArgumentException();
			}
		}
		if (tables.get(name) != null) {
			return null;
		}
		File table = new File(dbDirectory, name);
		if (table.isFile()) {
			throw new IllegalArgumentException();
		}
		try {
			table.mkdir();
			StructuredTableInterface savedTable = new StructuredStorage(table.getCanonicalPath(), name, columnTypes, this);
			tables.put(name, savedTable);
			return savedTable;
		}
		catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private boolean delete (String parent, String name) throws IOException {
		if (name == null) {
			return true;
		}
		File file = new File(parent, name);
		String[] content = file.list();
		if (content != null) {
			for (int i = 0; i < content.length; i++) {
				if (!delete(file.getCanonicalPath(), content[i])) {
					return false;
				}
			}
		}
		return file.delete();
	}

	public void removeTable (String name) throws IOException {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException();
		}
		File table = new File(dbDirectory, name);
		if (!table.isDirectory()) {
			throw new IllegalStateException(name + " not exists");
		}
		if (!delete(dbDirectory, name)) {
			throw new IllegalArgumentException();
		}
		tables.remove(name);
	}

	public Storeable deserialize (Table table, String value) throws ParseException {
		if (value == null) {
			return null;
		}
		value = value.trim();
		if (value.startsWith("<row>") && value.endsWith("</row>")) {
			value = value.substring(5, value.length() - 6);
		}
		else {
			throw new ParseException("not a valid XML", 0);
		}
		Storeable struct = createFor(table);
		for (int i = 0; value.length() > 0; i++) {
			if (value.startsWith("<null/>")) {
				continue;
			}
			int closeTag = value.indexOf("</col>");
			if (!value.startsWith("<col>") || closeTag == -1) {
				throw new ParseException("no matching close tag", 0);
			}
			String innerContent = value.substring(5, closeTag);
			String type = getNameByType(table.getColumnType(i));
			switch (type) {
				case "int":
					struct.setColumnAt(i, Integer.parseInt(innerContent));
					break;
				case "long":
					struct.setColumnAt(i, Long.parseLong(innerContent));
					break;
				case "float":
					struct.setColumnAt(i, Float.parseFloat(innerContent));
					break;
				case "byte":
					struct.setColumnAt(i, Byte.parseByte(innerContent));
					break;
				case "Double":
					struct.setColumnAt(i, Double.parseDouble(innerContent));
					break;
				case "Boolean":
					struct.setColumnAt(i, Boolean.parseBoolean(innerContent));
					break;
				case "String":
					struct.setColumnAt(i, innerContent);
					break;
				default:
					throw new ParseException("unknown type", 0);
			}
			value = value.substring(closeTag + 6);
		}
		return struct;
	}

	public String serialize (Table table, Storeable value) throws ColumnFormatException {
		if (value == null) {
			return null;
		}
		String result = "<row>";
		try {
			for (int i = 0; i < table.getColumnsCount(); i++) {
				Object obj = value.getColumnAt(i);
				if (obj == null) {
					result += "<null/>";
				}
				else {
					result += "<col>" + obj.toString() + "</col>";
				}
			}
		}
		catch (IndexOutOfBoundsException e) {
			throw new ColumnFormatException(e);
		}
		result += "</row>";
		return result;
	}

	public Storeable createFor (Table table) {
		return new StoreableInstance(table);
	}

	public Storeable createFor (Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
		return new StoreableInstance(table, values);
	}
};