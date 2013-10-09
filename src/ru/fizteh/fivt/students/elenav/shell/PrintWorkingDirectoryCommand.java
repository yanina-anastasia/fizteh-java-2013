package ru.fizteh.fivt.students.elenav.shell;

import java.io.IOException;

public class PrintWorkingDirectoryCommand extends AbstractCommand {
	PrintWorkingDirectoryCommand(ShellState s) { 
		setName("pwd"); 
		setArgNumber(0);
		setShell(s);
	}
	public void execute(String args[]) throws IOException {
		try {
			System.out.println(getWorkingDirectory().getCanonicalPath());
		} catch (SecurityException e) {
			throw new IOException(e.getMessage());
		}
	}
}
