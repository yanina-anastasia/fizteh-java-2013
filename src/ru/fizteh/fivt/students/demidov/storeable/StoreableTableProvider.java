package ru.fizteh.fivt.students.demidov.storeable;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.demidov.basicclasses.BasicTableProvider;

public class StoreableTableProvider extends BasicTableProvider<StoreableTable> implements TableProvider {
	public StoreableTableProvider(String root) {
		super(root);
	}
	
	public StoreableTable createTable(String name, List<Class<?>> columnTypes) throws IOException {
		if ((name == null) || (!(name.matches("\\w+")))) {
			throw new IllegalArgumentException("wrong table name " + name);
		}
		
		if ((columnTypes == null) || (columnTypes.isEmpty())) {
			throw new IllegalArgumentException("incorrect column types");
		}

		if (tables.containsKey(name)) {
			return null;
		} else {
			if (!(new File(root, name)).mkdir()) {
				throw new IllegalStateException("unable to make directory " + name);
			}
			
			try (PrintStream writtenSignature = new PrintStream(new File(root + File.separator + name, "signature.tsv"))) {
				for (int column = 0; column < columnTypes.size(); ++column) {
					Class<?> type = columnTypes.get(column);
					if (type == null) {
						throw new IllegalArgumentException("wrong column type");
					}	
					String typeName = null;
					try {
						typeName = TypeName.getAppropriateName(type);
					} catch(IOException catchedException) {
						throw new IllegalArgumentException("wrong column type");
					}

					writtenSignature.println(typeName);
				}
			}
			
			try {
				tables.put(name, new StoreableTable(root + File.separator + name, name, this, columnTypes));
			} catch(IOException catchedException) {
				throw new IllegalStateException(catchedException);
			}
		}	
		return tables.get(name);
	}

	public StoreableImplementation deserialize(Table table, String value) throws ParseException {
		try {
			return StoreableUtils.deserialize(table, value);
		} catch (XMLStreamException catchedException) {
			throw new ParseException(catchedException.getMessage(), 0);
		}
	}

	public String serialize(Table table, Storeable value) throws ColumnFormatException {
		try {
			return StoreableUtils.serialize(table, value);
		} catch (XMLStreamException catchedException) {
			throw new ColumnFormatException(catchedException);
		}
	}

	public StoreableImplementation createFor(Table table) {
		return new StoreableImplementation(table);
	}

	public StoreableImplementation createFor(Table table, List<?> values) throws ColumnFormatException {
		if (table.getColumnsCount() != values.size()) {
			throw new IndexOutOfBoundsException();
		}

		StoreableImplementation builtStoreable = createFor(table);
		for (int column = 0; column < table.getColumnsCount(); ++column) {
			builtStoreable.setColumnAt(column, values.get(column));
		}

		return builtStoreable;
	}

	public void readFilesMaps() throws IOException {
		for (String subdirectory : (new File(root)).list()) {
			if (!((new File(root, subdirectory)).isDirectory())) {
				throw new IOException("wrong directory " + subdirectory);
			} else {
				tables.put(subdirectory, new StoreableTable(root + File.separator + subdirectory, subdirectory, this));
				tables.get(subdirectory).getFilesMap().readData();
			}
		}
	}

	public void writeFilesMaps() throws IOException {
		for (String key: tables.keySet()) {
			StoreableTable table = tables.get(key);
			table.autoCommit();
			table.getFilesMap().writeData();
		}
	}
	
	public StoreableTable createTable(String name) {
		return null;
	}
}

