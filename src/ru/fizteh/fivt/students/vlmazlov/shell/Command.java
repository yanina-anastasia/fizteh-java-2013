package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.File;

public abstract class Command {
	private final String name;
	private final int argNum;

	protected final File getAbsFile(String file, Shell.ShellState state) {
		File absFile = new File(file);

		if (!absFile.isAbsolute()) {
			absFile = new File(new File(state.getCurDir()), file);
		}

		return absFile;
	}

	public String getName() {
		return name;
	}

	public int getArgNum() {
		return argNum;
	}

	Command(String _name, int _argNum) {
		name = _name;
		argNum = _argNum;
	}

	abstract public void execute(String[] args, Shell.ShellState state) throws CommandFailException, UserInterruptionException;
}