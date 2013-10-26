package ru.fizteh.fivt.students.elenav.commands;

import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class ExitCommand extends AbstractCommand {
	public ExitCommand(FilesystemState s) {
		super(s, "exit", 0);
	}
	
	public void execute(String[] args, PrintStream s) {
		throw new OutOfMemoryError();
	}
}
