package ru.fizteh.fivt.students.demidov.basicclasses;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import ru.fizteh.fivt.students.demidov.multifilehashmap.FilesMap;

abstract public class BasicTable<ElementType> {
	public BasicTable(String path, String tableName) throws IOException {
		this.filesMap = new FilesMap<ElementType>(path, this);
		this.tableName = tableName;
		putDiff = new HashMap<String, ElementType>();
		removeDiff = new HashSet<String>();
	}
	
	public String getName() {
		return tableName;
	}

	public ElementType get(String key) {
		if ((key == null) || (key.trim().isEmpty()) || (!(key.matches("\\s+")))) {
			throw new IllegalArgumentException("null or empty key");
		}
		ElementType value = filesMap.getFileMapForKey(key).getCurrentTable().get(key);
		if (putDiff.containsKey(key)) {
			value = putDiff.get(key);
		}
		if (removeDiff.contains(key)) {
			value = null;
		}
		return value;
	}

	public ElementType put(String key, ElementType value) {
		if ((key == null) || (key.trim().isEmpty()) || (value == null) || (!(key.matches("\\s+")))) {
			throw new IllegalArgumentException("null or empty parameter");
		}
		ElementType overwrite = get(key);
		ElementType previousValue = filesMap.getFileMapForKey(key).getCurrentTable().get(key);
		if (value.equals(previousValue)) {
			putDiff.remove(key);
		} else {
			putDiff.put(key, value);
		}
		removeDiff.remove(key);
		return overwrite;
	}

	public ElementType remove(String key) {
		if ((key == null) || (key.trim().isEmpty()) || (!(key.matches("\\s+")))) {
			throw new IllegalArgumentException("null or empty key");
		}
		ElementType removed = get(key);
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

	public int commit() throws IOException {	
		int changesNumber = getChangesNumber();
		if (changesNumber != 0) {
			autoCommit();
		}
		putDiff.clear();
		removeDiff.clear();
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
		putDiff.clear();
		removeDiff.clear();
		return changesNumber;  
	}

	public int getChangesNumber() {
		int removed = 0;
		Iterator<String> removeDiffIterator = removeDiff.iterator();
	    while(removeDiffIterator.hasNext()) {
	        String key = removeDiffIterator.next();
	        if (filesMap.getFileMapForKey(key).getCurrentTable().get(key) != null) {
	        	++removed;
	        }
	    }
		return putDiff.size() + removed;
	}
	
	public FilesMap<ElementType> getFilesMap() {
		return filesMap;
	}
	
	abstract public String serialize(ElementType value) throws IOException;
	
	abstract public ElementType deserialize(String value) throws IOException;
	
	private FilesMap<ElementType> filesMap;
	private String tableName;
	private Map<String, ElementType> putDiff;
	private Set<String> removeDiff;
}

