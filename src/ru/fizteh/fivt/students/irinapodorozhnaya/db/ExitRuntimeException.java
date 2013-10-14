package ru.fizteh.fivt.students.irinapodorozhnaya.db;

public class ExitRuntimeException extends RuntimeException{

	ExitRuntimeException() {
	}
	
	ExitRuntimeException(String message) {
		super(message);
	}
	
	ExitRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	ExitRuntimeException(Throwable cause) {
		super(cause);
	}
}
