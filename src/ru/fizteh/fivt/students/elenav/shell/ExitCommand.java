package ru.fizteh.fivt.students.elenav.shell;

import java.io.PrintStream;

public class ExitCommand extends AbstractCommand {
	ExitCommand(ShellState s) {
		super(s, "exit", 0);
	}
	public void execute(String[] args, PrintStream s) {
		System.exit(0);
	}
}
