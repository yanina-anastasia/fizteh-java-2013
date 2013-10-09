package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;

public abstract class Command {
	protected ShellState shell;
	protected String name;
	protected int argNumber;
	abstract void execute(String args[]) throws IOException;
	final String getName() {
		return name;
	}
	final int getArgNumber() {
		return argNumber;
	}
	
	public String absolutePath(String path) {
		File testPath = new File(shell.workingDirectory, path);
		return testPath.getAbsolutePath();
	}
}