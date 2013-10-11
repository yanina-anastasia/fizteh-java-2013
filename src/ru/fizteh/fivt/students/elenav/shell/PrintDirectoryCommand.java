package ru.fizteh.fivt.students.elenav.shell;

import java.io.PrintStream;

public class PrintDirectoryCommand extends AbstractCommand {
	PrintDirectoryCommand(ShellState s) { 
		super(s, "dir", 0);
	}
	public void execute(String[] args, PrintStream stream) {
		String[] files = getWorkingDirectory().list();
		for (String s : files) {
			stream.println(s);
		}
	}
}

