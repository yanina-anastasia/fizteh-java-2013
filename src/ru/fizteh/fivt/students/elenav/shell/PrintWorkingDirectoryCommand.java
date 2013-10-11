package ru.fizteh.fivt.students.elenav.shell;

import java.io.IOException;
import java.io.PrintStream;

public class PrintWorkingDirectoryCommand extends AbstractCommand {
	PrintWorkingDirectoryCommand(ShellState s) { 
		super(s, "pwd", 0);
	}
	public void execute(String args[], PrintStream s) throws IOException {
		try {
			s.println(getWorkingDirectory().getCanonicalPath());
		} catch (SecurityException e) {
			throw new IOException(e.getMessage());
		}
	}
}
