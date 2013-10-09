package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;

public abstract class AbstractCommand implements Command {
	private ShellState shell;
	private String name;
	private int argNumber;
	public String getName() {
		return name;
	}
	public void setName(String newName) {
		name = newName;
	}
	public void setArgNumber(int number) {
		argNumber = number;
	}
	public int getArgNumber() {
		return argNumber;
	}
	public void setShell(ShellState s) {
		shell = s;
	}
	public File getWorkingDirectory() {
		return shell.getWorkingDirectory();
	}
	public void setWorkingDirectory(File f) {
		shell.setWorkingDirectory(f);
	}
 	protected String absolutePath(String path) {
		File testPath = new File(getWorkingDirectory(), path);
		return testPath.getAbsolutePath();
	}
}