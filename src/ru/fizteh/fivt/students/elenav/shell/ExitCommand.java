package ru.fizteh.fivt.students.elenav.shell;

import java.io.PrintStream;

public class ExitCommand extends AbstractCommand {
	ExitCommand(ShellState s) {
		setName("exit");
		setArgNumber(0);
		setShell(s);
	}
	public void execute(String[] args, PrintStream s) {
		System.exit(0);
	}
}
