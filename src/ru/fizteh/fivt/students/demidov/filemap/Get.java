package ru.fizteh.fivt.students.demidov.filemap;

import java.io.IOException;

import ru.fizteh.fivt.students.demidov.basicclasses.BasicState;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Get extends BasicFileMapCommand {
	public Get(BasicState currentState) {
		super(currentState, "get", 1);
	}	
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException { 
		String value = currentState.get(arguments[0]);
		if (value == null) {
			usedShell.curShell.getOutStream().println("not found");
		} else {
			usedShell.curShell.getOutStream().println("found\n" + value);
		}
	}	
}
