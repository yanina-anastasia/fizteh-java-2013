package ru.fizteh.fivt.students.vlmazlov.filemap;

import ru.fizteh.fivt.students.vlmazlov.shell.Shell;
import ru.fizteh.fivt.storage.strings.Table;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.DiffCountingTable;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityChecker;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityCheckFailedException;

public class FileMap implements Iterable<Map.Entry<String, String>>, DiffCountingTable {

	private Map<String, String> commited, added;
	private Set<String> deleted;
	private String name;
	private int diff;
	private final boolean autoCommit;

	public FileMap(String name) {
		this.name = name;
		commited = new HashMap<String, String>();
		added = new HashMap<String, String>();
		deleted = new HashSet<String>();
		this.autoCommit = true;
	}

	public FileMap(String name, boolean autoCommit) {
		this.name = name;
		commited = new HashMap<String, String>();
		added = new HashMap<String, String>();
		deleted = new HashSet<String>();
		this.autoCommit = autoCommit;
	}

	//iterating is possible only for committed elements,
	//which guarantees that uncommited changes won't be written when exit occurs
	@Override
	public Iterator<Map.Entry<String, String>> iterator() {
		return commited.entrySet().iterator();
	}


	@Override
	public String put(String key, String value) {
		try {
			ValidityChecker.checkFileMapKey(key);
			ValidityChecker.checkFileMapValue(value);
		} catch (ValidityCheckFailedException ex) {
			throw new IllegalArgumentException(ex.getMessage());
		}

		String oldValue = commited.get(key);
		String returnValue = null;	

		//overwriting the key from the last commit
		if ((!deleted.contains(key)) && (oldValue != null) && (added.get(key) == null)) {
			if (!value.equals(oldValue)) {
				++diff;
				added.put(key, value);
			}

			returnValue = oldValue;

		//adding a new key
		} else if ((oldValue == null) && (added.get(key) == null)) {
			++diff;
			returnValue = added.put(key, value);

		//adding back something added after the commit that was deleted
		} else if (deleted.contains(key)) {
			deleted.remove(key);

			if (!oldValue.equals(value)) {
				added.put(key, value);
			} else {
				--diff;
			}
			
			returnValue = null;

		//overwriting a key from the last commit not for the first time
		} else if ((oldValue != null) && (added.get(key) != null)) {
			returnValue = added.put(key, value);

		//overwriting a key added after the last commit
		} else if ((oldValue == null) && (added.get(key) != null)) {
			returnValue = added.put(key, value);
		}

		if (autoCommit) {
			commit();
		}
		return returnValue;
	}

	@Override
	public String get(String key) {
		try {
			ValidityChecker.checkFileMapKey(key);
		} catch (ValidityCheckFailedException ex) {
			throw new IllegalArgumentException(ex.getMessage());
		}

		if (added.get(key) != null) {
			return added.get(key);
		} else if ((commited.get(key) != null) && (!deleted.contains(key))) {
			return commited.get(key);
		}

		return null;
	}
 
 	@Override
	public String remove(String key) {
		try {
			ValidityChecker.checkFileMapKey(key);
		} catch (ValidityCheckFailedException ex) {
			throw new IllegalArgumentException(ex.getMessage());
		}

		String oldValue = commited.get(key);
		String returnValue = null;

		//remove key from last commit
		if ((oldValue != null) && (added.get(key) == null)) {
			deleted.add(key);
			++diff;
			returnValue = oldValue;

		//remove a key that was added after the last commit
		} else if ((oldValue == null) && (added.get(key) != null)) {
			--diff;
			returnValue = added.remove(key);
		} else {

			String previousValue = added.remove(key);
			
			if (oldValue != null) {
				deleted.add(key);
			}
			
			returnValue = previousValue;
		}

		if (autoCommit) {
			commit();
		}
		return returnValue;
	}

	@Override
	public int size() {
		return commited.size() + added.size();
	}

	@Override
	public String getName() {
		return name;
	}

	//Unsupported functionality

	@Override
	public int commit() {
		for (Map.Entry<String, String> entry: added.entrySet()) {
			commited.put(entry.getKey(), entry.getValue());
		}

		for (String entry : deleted) {
			commited.remove(entry);
		}

		int diffNum = diff;
		diff = 0;

		added.clear();
		deleted.clear();

		return diffNum;
	}

	//Unsupported functionality

	@Override
    public int rollback() {
    	added.clear();
    	deleted.clear();

    	int diffNum = diff;
		diff = 0;

		return diffNum;
    }

    @Override
	public int getDiffCount() {
		return diff;
	}
}