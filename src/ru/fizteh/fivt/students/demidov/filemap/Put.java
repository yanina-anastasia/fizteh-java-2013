package ru.fizteh.fivt.students.demidov.filemap;


import java.io.IOException;

import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Put extends BasicFileMapCommand {
	public Put(BasicState currentState) {
		super(currentState);
	}
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {    
		String value = null;
		try {
			value = currentState.getUsedTable().put(arguments[0], arguments[1]);
		} catch (IllegalArgumentException catchedException) {
			throw new IOException(catchedException); 
		}
		if (value == null) {
			usedShell.curShell.getOutStream().println("new");
		} else {
			usedShell.curShell.getOutStream().println("overwrite\n" + value);
		}
	}	
	public int getNumberOfArguments() {
		return 2;
	}	
	public String getCommandName() {
		return "put";
	}
}