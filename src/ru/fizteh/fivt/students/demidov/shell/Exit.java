package ru.fizteh.fivt.students.demidov.shell;

public class Exit implements BasicCommand {
	public void executeCommand(String[] arguments) throws InterruptionException {    
		throw new InterruptionException();
	}
}
