package ru.fizteh.fivt.students.vlmazlov.filemap;

import java.io.FileNotFoundException;

public class StorageNotFoundException extends FileNotFoundException {
	public StorageNotFoundException() { 
		super(); 
	}
	
	public StorageNotFoundException(String message) { 
		super(message); 
	}
	
}