package ru.fizteh.fivt.students.elenav.storeable;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;
import ru.fizteh.fivt.students.elenav.states.Provider;
import ru.fizteh.fivt.students.elenav.utils.Functions;

public class StoreableTableProvider implements TableProvider, Provider {

	private final String CORRECT_FORMAT = "[a-zA-Zа-яА-Я0-9]+";
	private File workingDirectory = null;
	private PrintStream stream;
	public HashMap<String, StoreableTableState> tables = new HashMap<>();
	
	public StoreableTableProvider(File dir, PrintStream out) throws IOException {
		if (dir == null) {
			throw new IllegalArgumentException("can't create null table");
		}
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("can't create null table");
		}
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				tables.put(f.getName(), new StoreableTableState(f.getName(), f, out, this));
			}
		}
		setWorkingDirectory(dir);
		setStream(out);
	}

	private boolean isCorrectColumnType(List<Class<?>> columnTypes) {
		for (Class<?> type : columnTypes) {
			if (type == null) {
				return false;
			}
			String stringType = type.getSimpleName();
			if (!stringType.equals("Integer") && !stringType.equals("Long") && !stringType.equals("Byte") &&
					!stringType.equals("Float") && !stringType.equals("Double") && !stringType.equals("Boolean") 
						&& !stringType.equals("String")) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public Table getTable(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("can't get table with null name");
		}
		if (!name.matches(CORRECT_FORMAT)) {
			throw new IllegalArgumentException("can't get table with invalid name");
		}
		return tables.get(name);
	}

	@Override
	public StoreableTableState createTable(String name, List<Class<?>> columnTypes) throws IOException {
		if (name == null || name.trim().isEmpty() || !name.matches(CORRECT_FORMAT)) {
			throw new IllegalArgumentException("can't create table: null or invalid name");
		}
		if (columnTypes == null || columnTypes.isEmpty()) {
			throw new IllegalArgumentException("can't create table: null or empty columnTypes");
		}
		if (!isCorrectColumnType(columnTypes)) {
			throw new IllegalArgumentException("can't create table: invalid columnTypes");
		}
		File f = new File(getWorkingDirectory(), name);
		if (f.exists()) {
			return null;
		}
		if (!f.mkdir()) {
			throw new IOException("can't create table: unknown error");
		}
		setSignature(f, columnTypes);
		StoreableTableState table = new StoreableTableState(name, f, getStream(), this);
		tables.put(name, table);
		return table;
	}

	private void setSignature(File f, List<Class<?>> types) {
		File signature = new File(f, "signature.tsv");
		try {
			f.createNewFile();
			PrintStream s = new PrintStream(signature);
			for (int i = 0; i < types.size(); ++i) {
				s.append(Functions.getStringFromClass(types.get(i)));
				s.append(" ");
			}
			s.close();
		} catch (IOException e) {
			throw new RuntimeException("can't set signature for file "+f.getName());
		}
			
	}

	@Override
	public void removeTable(String name) throws IOException {
		if (name == null || name.trim().isEmpty() || !name.matches(CORRECT_FORMAT)) {
            throw new IllegalArgumentException("can't remove table: invalid name");
        }
		if (tables.get(name) == null) {
			throw new IllegalStateException("can't remove table: table not exist");
		}
		try {
			Functions.deleteRecursively(tables.get(name).getWorkingDirectory());
		} catch (IOException e) {
			throw new IOException(e);
		}
		tables.remove(name);
		
	}

	@Override
	public Storeable deserialize(Table table, String value) throws ParseException {
		if (table == null || value == null) {
			throw new IllegalArgumentException("can't deserialize: null table or value");
		}
		try {
			return Deserializer.run(table, value);
		} catch (XMLStreamException e) {
			throw new ParseException(e.getMessage(), 0);
		}
	}

	@Override
	public String serialize(Table table, Storeable value) throws ColumnFormatException {
		if (table == null || value == null) {
			throw new IllegalArgumentException("can't serialize: null table or value");
		}
		try {
			return Serializer.run(table, value);
		} catch (XMLStreamException e) {
			throw new ColumnFormatException(e);
		}
	}

	@Override
	public Storeable createFor(Table table) {
		return new MyStoreable(table);
	}

	@Override
	public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
		Storeable storeable = new MyStoreable(table);
		for (int i = 0; i < table.getColumnsCount(); ++i) {
			storeable.setColumnAt(i, values.get(i));
		}
		return storeable;
	}

	public File getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public PrintStream getStream() {
		return stream;
	}

	public void setStream(PrintStream stream) {
		this.stream = stream;
	}

	@Override
	public FilesystemState createTable(String string) {
		System.err.println("Command can't be executed");
		return null;
	}

	@Override
	public void use(FilesystemState table) throws IOException {
		StoreableTableState.class.cast(table).getColumnTypes();
		
	}

	
}
