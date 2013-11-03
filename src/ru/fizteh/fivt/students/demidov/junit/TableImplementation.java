package ru.fizteh.fivt.students.demidov.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.demidov.multifilehashmap.FilesMap;

public class TableImplementation implements Table {
	public TableImplementation(FilesMap filesMap, String tableName) {
		this.filesMap = filesMap;
		this.tableName = tableName;
		changesNumber = 0;
	}
	
    public String getName() {
    	return tableName;
    }

    public String get(String key) {
    	if (key == null) {
    		throw new IllegalArgumentException("null key");
    	}
    	return filesMap.getFileMapForKey(key).getCurrentTable().get(key);
    }

    public String put(String key, String value) {
    	if ((key == null) || (value == null)) {
    		throw new IllegalArgumentException("null parameter");
    	}
    	++changesNumber;
   		return filesMap.getFileMapForKey(key).getCurrentTable().put(key, value);
    }

    public String remove(String key) {
    	if (key == null) {
    		throw new IllegalArgumentException("null key");
    	}
    	++changesNumber;
   		return filesMap.getFileMapForKey(key).getCurrentTable().remove(key);
    }

    public int size() {
    	return filesMap.getSize();
    }

    public int commit() {    	
    	if (changesNumber != 0) {
    		filesMap.commitChanges();
    	}
    	int changesNum = changesNumber;
    	changesNumber = 0;
    	return changesNum;    	
    }

    public int rollback() {
    	filesMap.rollbackChanges();
    	int changesNum = changesNumber;
    	changesNumber = 0;
    	return changesNum;  
    }
    
    public int getChangesNumber() {
    	return changesNumber;
    }
    
    public FilesMap getFilesMap() {
    	return filesMap;
    }
	
	private FilesMap filesMap;
	private String tableName;
	private int changesNumber;
}
