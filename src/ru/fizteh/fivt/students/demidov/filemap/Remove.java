package ru.fizteh.fivt.students.demidov.filemap;

import java.io.IOException;

import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Remove extends BasicFileMapCommand {
	public Remove(BasicState currentState) {
		super(currentState);
	}
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {    
		String value = null;
		try {
			value = currentState.getUsedTable().remove(arguments[0]);
		} catch (IllegalArgumentException catchedException) {
			throw new IOException(catchedException); 
		}
		if (value == null) {
			usedShell.curShell.getOutStream().println("not found");
		} else {
			usedShell.curShell.getOutStream().println("removed");
		}
	}	
	public int getNumberOfArguments() {
		return 1;
	}	
	public String getCommandName() {
		return "remove";
	}
}