package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.File;
import java.io.OutputStream;

public abstract class AbstractCommand implements Command {
	private final String name;
	private final int argNum;

	public String getName() {
		return name;
	}

	public int getArgNum() {
		return argNum;
	}

	AbstractCommand(String _name, int _argNum) {
		name = _name;
		argNum = _argNum;
	}

	abstract public void execute(String[] args, Shell.ShellState state, OutputStream out) throws CommandFailException, UserInterruptionException;
}