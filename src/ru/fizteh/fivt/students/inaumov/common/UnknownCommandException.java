package ru.fizteh.fivt.students.inaumov.common;

public class UnknownCommandException extends Exception {
	UnknownCommandException() {
		super();
	}
	
	UnknownCommandException(String message) {
		super(message);
	}
}
