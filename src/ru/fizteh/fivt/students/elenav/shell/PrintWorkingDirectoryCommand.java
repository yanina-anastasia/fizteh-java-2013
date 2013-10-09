package ru.fizteh.fivt.students.elenav.shell;

import java.io.IOException;

class PrintWorkingDirectoryCommand extends Command {
	PrintWorkingDirectoryCommand(ShellState s) { 
		name = "pwd"; 
		argNumber = 0;
		shell = s;
	}
	void execute(String args[]) throws IOException {
		try {
			System.out.println(shell.workingDirectory.getCanonicalPath());
		} catch (SecurityException e) {
			throw new IOException(e.getMessage());
		}
	}
}
