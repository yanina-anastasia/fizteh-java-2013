package ru.fizteh.fivt.students.demidov.shell;

import ru.fizteh.fivt.students.demidov.shell.BasicCommand;
import ru.fizteh.fivt.students.demidov.shell.InterruptionException;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Exit implements BasicCommand {
	public void executeCommand(String[] arguments, Shell usedShell) throws InterruptionException {    
		throw new InterruptionException();
	}	
	public int getNumberOfArguments() {
		return 0;
	}	
	public String getCommandName() {
		return "exit";
	}
}
