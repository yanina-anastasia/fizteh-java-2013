package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.OutputStream;

public interface Command {
	public String getName();
	public int getArgNum();
	public void execute(String[] args, Shell.ShellState state, OutputStream out) throws CommandFailException, UserInterruptionException;

}