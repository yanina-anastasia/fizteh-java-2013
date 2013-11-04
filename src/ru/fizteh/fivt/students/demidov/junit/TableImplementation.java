package ru.fizteh.fivt.students.demidov.junit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.demidov.multifilehashmap.FilesMap;

public class TableImplementation implements Table {
	public TableImplementation(FilesMap filesMap, String tableName) {
		this.filesMap = filesMap;
		this.tableName = tableName;
		putDiff = new HashMap<String, String>();
		removeDiff = new HashSet<String>();
	}
	
	public String getName() {
		return tableName;
	}

	public String get(String key) {
		if ((key == null) || (key.trim().isEmpty())) {
			throw new IllegalArgumentException("null or empty key");
		}
		String value = filesMap.getFileMapForKey(key).getCurrentTable().get(key);
		if (putDiff.containsKey(key)) {
			value = putDiff.get(key);
		}
		if (removeDiff.contains(key)) {
			value = null;
		}
		return value;
	}

	public String put(String key, String value) {
		if ((key == null) || (key.trim().isEmpty()) || (value == null) || (value.trim().isEmpty())) {
			throw new IllegalArgumentException("null or empty parameter");
		}
		String overwritten = get(key);
		putDiff.put(key, value);
		removeDiff.remove(key);
		return overwritten;
	}

	public String remove(String key) {
		if ((key == null) || (key.trim().isEmpty())) {
			throw new IllegalArgumentException("null or empty key");
		}
		String removed = get(key);
		if (removed != null) {
			removeDiff.add(key);
		}
		putDiff.remove(key);
		return removed;
	}

	public int size() {
		int previousSize = filesMap.getSize();
		for (String key: putDiff.keySet()) {
			if (filesMap.getFileMapForKey(key).getCurrentTable().get(key) == null) {
				++previousSize;
			}
		}	
		Iterator<String> removeDiffIterator = removeDiff.iterator();
	    while(removeDiffIterator.hasNext()){
	        String key = removeDiffIterator.next();
	        if (filesMap.getFileMapForKey(key).getCurrentTable().get(key) != null) {
	        	--previousSize;
	        }
	    }
		return previousSize;
	}

	public int commit() {	
		int changesNumber = getChangesNumber();
		if (changesNumber != 0) {
			autoCommit();
		}
		putDiff = new HashMap<String, String>();
		removeDiff = new HashSet<String>();
		return changesNumber;	
	}
	
	public void autoCommit() {
		for (String key: putDiff.keySet()) {
			filesMap.getFileMapForKey(key).getCurrentTable().put(key, putDiff.get(key));
		}		
		Iterator<String> removeDiffIterator = removeDiff.iterator();
	    while(removeDiffIterator.hasNext()){
	        String key = removeDiffIterator.next();
	        filesMap.getFileMapForKey(key).getCurrentTable().remove(key);
	    }
	}

	public int rollback() {
		int changesNumber = getChangesNumber();
		putDiff = new HashMap<String, String>();
		removeDiff = new HashSet<String>();
		return changesNumber;  
	}

	public int getChangesNumber() {
		return putDiff.size() + removeDiff.size();
	}
	
	public FilesMap getFilesMap() {
		return filesMap;
	}
	
	private FilesMap filesMap;
	private String tableName;
	private Map<String, String> putDiff;
	private Set<String> removeDiff;
}
