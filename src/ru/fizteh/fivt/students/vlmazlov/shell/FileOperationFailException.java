package ru.fizteh.fivt.students.vlmazlov.shell;

public class FileOperationFailException extends Exception {
	public FileOperationFailException() { 
		super(); 
	}
	
	public FileOperationFailException(String message) { 
		super(message); 
	}
	
	public FileOperationFailException(String message, Throwable cause) { 
		super(message, cause); 
	}
	
	public FileOperationFailException(Throwable cause) { 
		super(cause); 
	}
}