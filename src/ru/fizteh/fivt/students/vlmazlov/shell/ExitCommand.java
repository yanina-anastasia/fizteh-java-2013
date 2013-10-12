package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.OutputStream;

public class ExitCommand extends AbstractCommand {
	public ExitCommand() {
		super("exit", 0);
	};	

	public void execute(String[] args, Shell.ShellState state, OutputStream out) throws UserInterruptionException {		
		throw new UserInterruptionException();
	}
}