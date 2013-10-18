package ru.fizteh.fivt.students.elenav.commands;

import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.shell.FilesystemState;

public class ExitShellCommand extends AbstractCommand {
	public ExitShellCommand(FilesystemState s) {
		super(s, "exit", 0);
	}
	
	public void execute(String[] args, PrintStream s) {
		System.exit(0);
	}
}
