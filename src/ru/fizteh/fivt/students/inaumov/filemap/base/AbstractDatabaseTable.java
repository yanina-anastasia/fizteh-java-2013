package ru.fizteh.fivt.students.inaumov.filemap.base;

import java.io.IOException;
import java.util.HashMap;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class AbstractDatabaseTable<Key, Value> {
    // кодировка
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    // сохраненная информация
	public HashMap<Key, Value> keyValueHashMap = new HashMap<Key, Value>();
    // измененная информация
	public HashMap<Key, OldAndNewValue<Value>> modifiedKeyValueHashMap = new HashMap<Key,  OldAndNewValue<Value>>();
    // сохранненное и измененное значение
    private class OldAndNewValue<Value> {
        public Value oldValue;
        public Value newValue;

        OldAndNewValue(Value oldValue, Value newValue) {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
    }
    // название таблицы
	final private String tableName;
    // директория таблицы
	final private String tableDir;
    // размер таблицы
	private int tableSize = 0;
    // количество несохраненных изменений
	private int unsavedChangesNumber = 0;

    // загрузить таблицу
	protected abstract void loadTable() throws IOException;
    // сохранить таблицу
	protected abstract void saveTable() throws IOException;

	public AbstractDatabaseTable(String tableDir, String tableName) {
		if (tableDir == null || tableDir.isEmpty()) {
			throw new IllegalArgumentException("error: selected directory is null (or empty)");
		}
		if (tableName == null || tableName.isEmpty()) {
			throw new IllegalArgumentException("error: selected database name is null (or empty)");
		}
		
		this.tableName = tableName;
		this.tableDir = tableDir;

        try {
		    loadTable();
        } catch (IOException e) {
            throw new IllegalArgumentException("incorrect file format");
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
			throw new IllegalArgumentException("key can't be null");
		}
		
		if (modifiedKeyValueHashMap.containsKey(key)) {
			return modifiedKeyValueHashMap.get(key).newValue;
		}

		return keyValueHashMap.get(key);
	}

	public Value tablePut(Key key, Value value) {
		if (key == null) {
			throw new IllegalArgumentException("key can't be null or empty");
		}
		if (value == null) {
			throw new IllegalArgumentException("value can't be null or empty");
		}
		
		Value oldValue = getOldValue(key);
        if (oldValue == null) {
            tableSize += 1;
        }

        makeChange(key, value);

        unsavedChangesNumber += 1;

		return oldValue;
	}

	public Value tableRemove(Key key) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException("key can't be null");
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
            OldAndNewValue oldAndNewValue = modifiedKeyValueHashMap.get(key);
            if (!isEqual(oldAndNewValue.newValue, oldAndNewValue.oldValue)) {
                if (oldAndNewValue.newValue == null) {
                    keyValueHashMap.remove(key);
                } else {
                    keyValueHashMap.put(key, (Value) oldAndNewValue.newValue);
                }

                savedChangesNumber += 1;
            }
        }

        modifiedKeyValueHashMap.clear();

        tableSize = keyValueHashMap.size();

        try {
            saveTable();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return 0;
        }

        unsavedChangesNumber = 0;

        return savedChangesNumber;
    }

    public int tableRollback() {
        int rollbackChangesNumber = 0;

        for (final Key key: modifiedKeyValueHashMap.keySet()) {
            OldAndNewValue oldAndNewValue = modifiedKeyValueHashMap.get(key);
            if (!isEqual(oldAndNewValue.newValue, oldAndNewValue.oldValue)) {
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
            modifiedKeyValueHashMap.put(key, new OldAndNewValue(keyValueHashMap.get(key), value));
        }
    }

    private Value getOldValue(Key key) {
        if (modifiedKeyValueHashMap.containsKey(key)) {
            return modifiedKeyValueHashMap.get(key).newValue;
        }

        return keyValueHashMap.get(key);
    }

    private boolean isEqual(Object o1, Object o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }

    public void rawPut(Key key, Value value) {
        keyValueHashMap.put(key, value);
    }

    public Value rawGet(Key key) {
        return keyValueHashMap.get(key);
    }
}