package ru.fizteh.fivt.students.elenav.commands;

import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.shell.State;

public class ExitCommand extends AbstractCommand {
	public ExitCommand(State s) {
		super(s, "exit", 0);
	}
	
	public void execute(String[] args, PrintStream s) {
		System.exit(0);
	}
}
