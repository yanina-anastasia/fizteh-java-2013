package ru.fizteh.fivt.students.inaumov.filemap.base;

import ru.fizteh.fivt.students.inaumov.filemap.FileMapUtils;

import java.io.IOException;
import java.util.HashMap;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class AbstractDatabaseTable<Key, Value> {
    public static final Charset CHARSET = StandardCharsets.UTF_8;
	public HashMap<Key, Value> keyValueHashMap = new HashMap<Key, Value>();
	public HashMap<Key, ValueDiff<Value>> modifiedKeyValueHashMap = new HashMap<Key, ValueDiff<Value>>();

    private class ValueDiff<Value> {
        public Value oldValue;
        public Value newValue;

        ValueDiff(Value oldValue, Value newValue) {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
    }

	final private String tableName;
	final private String tableDir;
	private int tableSize = 0;
	private int unsavedChangesNumber = 0;

	protected abstract void loadTable() throws IOException;

	protected abstract void saveTable() throws IOException;

	public AbstractDatabaseTable(String tableDir, String tableName) {
		if (FileMapUtils.isStringNullOrEmpty(tableDir)) {
			throw new IllegalArgumentException("error: selected directory is null (or empty)");
		}
		if (FileMapUtils.isStringNullOrEmpty(tableName)) {
			throw new IllegalArgumentException("error: selected database name is null (or empty)");
		}
		
		this.tableName = tableName;
		this.tableDir = tableDir;

        try {
		    loadTable();
        } catch (IOException e) {
            throw new IllegalArgumentException("error: can't load table, incorrect file format");
        }
	}
	
	public String getName() {
		return tableName;
	}

	public String getDir() {
		return tableDir;
	}
	
	public Value tableGet(Key key) {
		if (key == null) {
			throw new IllegalArgumentException("error: selected key is null");
		}
		
		if (modifiedKeyValueHashMap.containsKey(key)) {
			return modifiedKeyValueHashMap.get(key).newValue;
		}

		return keyValueHashMap.get(key);
	}

	public Value tablePut(Key key, Value value) {
		if (key == null) {
			throw new IllegalArgumentException("error: selected key is null");
		}
		if (value == null) {
			throw new IllegalArgumentException("error: selected value is null");
		}
		
		Value oldValue = getOldValue(key);
        if (oldValue == null) {
            tableSize += 1;
        }

        if (!FileMapUtils.isEqual(oldValue, value)) {
            unsavedChangesNumber += 1;
        }

        makeChange(key, value);

		return oldValue;
	}

	public Value tableRemove(Key key) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException("error: selected key is null");
		}
		
		if (tableGet(key) == null) {
            return null;
        }

        Value oldValue = getOldValue(key);
        makeChange(key, null);

        if (oldValue != null) {
            tableSize -= 1;
        }

        unsavedChangesNumber += 1;

		return oldValue;
	}

    public int tableCommit() {
        int savedChangesNumber = 0;

        for (final Key key: modifiedKeyValueHashMap.keySet()) {
            ValueDiff valueDiff = modifiedKeyValueHashMap.get(key);
            if (!FileMapUtils.isEqual(valueDiff.newValue, valueDiff.oldValue)) {
                if (valueDiff.newValue == null) {
                    keyValueHashMap.remove(key);
                } else {
                    keyValueHashMap.put(key, (Value) valueDiff.newValue);
                }

                savedChangesNumber += 1;
            }
        }

        modifiedKeyValueHashMap.clear();
        tableSize = keyValueHashMap.size();

        try {
            saveTable();
        } catch (IOException e) {
            System.err.println("error: can't save table: " + e.getMessage());
            return 0;
        }

        unsavedChangesNumber = 0;

        return savedChangesNumber;
    }

    public int tableRollback() {
        int rollbackChangesNumber = 0;

        for (final Key key: modifiedKeyValueHashMap.keySet()) {
            ValueDiff valueDiff = modifiedKeyValueHashMap.get(key);
            if (!FileMapUtils.isEqual(valueDiff.newValue, valueDiff.oldValue)) {
                rollbackChangesNumber += 1;
            }
        }

        modifiedKeyValueHashMap.clear();
        tableSize = keyValueHashMap.size();
        unsavedChangesNumber = 0;

        return rollbackChangesNumber;
    }

	public int tableSize() {
		return tableSize;
	}

	public int getUnsavedChangesNumber() {
		return unsavedChangesNumber;
	}

    private void makeChange(Key key, Value value) {
        if (modifiedKeyValueHashMap.containsKey(key)) {
            modifiedKeyValueHashMap.get(key).newValue = value;
        } else {
            modifiedKeyValueHashMap.put(key, new ValueDiff(keyValueHashMap.get(key), value));
        }
    }

    private Value getOldValue(Key key) {
        if (modifiedKeyValueHashMap.containsKey(key)) {
            return modifiedKeyValueHashMap.get(key).newValue;
        }

        return keyValueHashMap.get(key);
    }

    public void rawPut(Key key, Value value) {
        keyValueHashMap.put(key, value);
    }

    public Value rawGet(Key key) {
        return keyValueHashMap.get(key);
    }
}