package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import java.util.HashMap;
import java.util.Iterator;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Map;
import ru.fizteh.fivt.students.vlmazlov.filemap.FileMap;

public class MultiTableDataBase {
	private HashMap<String, FileMap> tables;
	private FileMap activeTable;
	private final File root;

	public MultiTableDataBase(String rootPath) throws FileNotFoundException, ValidityCheckFailedException {
		if (rootPath == null) {
			throw new FileNotFoundException("Root directory not specified");
		}
		
		File root = new File(rootPath);
		
		ValidityChecker.checkMultiTableRoot(root);

		this.root = root;
		tables = new HashMap<String, FileMap>();
		activeTable = null;
	}

	public FileMap getTable(String tablename) {
		return tables.get(tablename);
	}

	public FileMap getActiveTable() {
		return activeTable;
	}

	public boolean use(String tablename) {
		FileMap newActive = tables.get(tablename);
		if (newActive == null) {
			return false;
		}

		activeTable = newActive;
		return true;
	}

	public void create(String tablename) {
		tables.put(tablename, new FileMap());
	}

	public File drop(String tablename) {
		FileMap oldTable = tables.remove(tablename);

		if (oldTable == null) {
			return null;
		} else {
			if (oldTable.equals(activeTable)) {
				activeTable = null;
			}

			return new File(root, tablename);
		}
	}

	public File getRoot() {
		return root;
	}
}