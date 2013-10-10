package ru.fizteh.fivt.students.elenav.shell;

import java.io.PrintStream;

public class PrintDirectoryCommand extends AbstractCommand {
	PrintDirectoryCommand(ShellState s) { 
		setName("dir"); 
		setArgNumber(0);
		setShell(s);
	}
	public void execute(String[] args, PrintStream stream) {
		String[] files = getWorkingDirectory().list();
		for (String s : files) {
			stream.println(s);
		}
	}
}

