//package ru.fizteh.fivt.students.inaumov.shell;

public class UnknownCommandException extends Exception{
	UnknownCommandException() {
		super();
	}
	UnknownCommandException(String exceptionMessage) {
		super(exceptionMessage);
	}
}
