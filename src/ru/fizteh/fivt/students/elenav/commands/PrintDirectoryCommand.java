package ru.fizteh.fivt.students.elenav.commands;

import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.shell.State;

public class PrintDirectoryCommand extends AbstractCommand {
	public PrintDirectoryCommand(State s) { 
		super(s, "dir", 0);
	}
	
	public void execute(String[] args, PrintStream stream) {
		String[] files = getState().getWorkingDirectory().list();
		for (String s : files) {
			stream.println(s);
		}
	}
}

