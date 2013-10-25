package ru.fizteh.fivt.students.inaumov.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import java.io.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class AbstractTable implements Table {
    protected static final Charset CHARSET = StandardCharsets.UTF_8;

	protected HashMap<String, String> tableHash = new HashMap<String, String>();
	protected HashMap<String, String> modifiedTableHash = new HashMap<String, String>();
	protected HashSet<String> deleted = new HashSet<String>();
	
	private String tableName;
	private String dir;
	
	private int tableSize = 0;
	private int unsavedChangesNumber = 0;
	
	public WriteHandler writeHandler;
	public ReadHandler readHandler;

	public abstract void loadTable() throws IOException;
	public abstract void saveTable() throws IOException;
	
	public AbstractTable(String dir, String tableName) throws IOException, IllegalArgumentException {
		if (dir == null) {
			throw new IllegalArgumentException("directory can't be null");
		}
		if (tableName == null) {
			throw new IllegalArgumentException("table name can't be null");
		}
		
		this.tableName = tableName;
		this.dir = dir;
		
		loadTable();
	}
	
	public String getName() {
		return tableName;
	}

	public String getDir() {
		return dir;
	}
	
	public String get(String key) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException("key can't be null");
		}
		
		if (modifiedTableHash.containsKey(key)) {
			return modifiedTableHash.get(key);
		}
		if (tableHash.containsKey(key) && !deleted.contains(key)) {
			return tableHash.get(key);
		}

		return null;
	}

	public String put(String key, String value) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException("key can't be null");
		}
		if (value == null) {
			throw new IllegalArgumentException("value can't be null");
		}
		
		if (!modifiedTableHash.containsKey(key) && !tableHash.containsKey(key)
			|| tableHash.containsKey(key) && deleted.contains(key)) {
			tableSize += 1;
		}

		String oldValue = getOldValue(key);
        modifiedTableHash.put(key, value);
		unsavedChangesNumber += 1;
		
		return oldValue;
	}

	public String remove(String key) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException("key can't be null");
		}
		
		String oldValue = null;
		if (modifiedTableHash.containsKey(key)) {
			oldValue = modifiedTableHash.get(key);
			modifiedTableHash.remove(key);
			unsavedChangesNumber += 1;
			tableSize -= 1;
			
			return oldValue;
		}
		if (tableHash.containsKey(key) && !deleted.contains(key)) {
			oldValue = tableHash.get(key);
			deleted.add(key);
			unsavedChangesNumber += 1;
			tableSize -= 1;
			
			return oldValue;
		}
		
		return oldValue;
	}

	public int size() {
		return tableSize;
	}

	public int getUnsavedChangesNumber() {
		return unsavedChangesNumber;
	}

    private String getOldValue(String key) {
        String oldValue = modifiedTableHash.get(key);
        if (oldValue == null && !deleted.contains(key)) {
            oldValue =  tableHash.get(key);
        }

        return oldValue;
    }

	public int commit() {
		for (Map.Entry<String, String> nextEntry: modifiedTableHash.entrySet()) {
			tableHash.put(nextEntry.getKey(), nextEntry.getValue());
		}
		for (String nextEntry: deleted) {
			tableHash.remove(nextEntry);
		}
		modifiedTableHash.clear();
		deleted.clear();
		
		try {
			saveTable();
		} catch (IOException exception) {
			System.err.println(exception.getMessage());
			return 0;
		}
		
		int savedChangesNumber = unsavedChangesNumber;
		unsavedChangesNumber = 0;
		
		return savedChangesNumber;
	}

	public int rollback() {
		modifiedTableHash.clear();

		tableSize += deleted.size();
		
		deleted.clear();
		
		int rollbackedChangesNumber = unsavedChangesNumber;
		unsavedChangesNumber = 0;
		
		return rollbackedChangesNumber;
	}
}