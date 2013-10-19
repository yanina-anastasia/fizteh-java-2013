package ru.fizteh.fivt.students.inaumov.shell;

public class CommandExecutionFailException extends Exception {
	public CommandExecutionFailException() {
		super();
	}
	public CommandExecutionFailException(String exceptionMessage) {
		super(exceptionMessage);
	}
}
