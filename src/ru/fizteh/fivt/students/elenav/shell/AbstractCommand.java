package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;

public abstract class AbstractCommand implements Command {
	private final ShellState shell;
	private final String name;
	private final int argNumber;
	
	AbstractCommand(ShellState s, String nm, int n) {
		shell = s;
		name = nm;
		argNumber = n;
	}
	public String getName() {
		return name;
	}
	public int getArgNumber() {
		return argNumber;
	}
	public File getWorkingDirectory() {
		return shell.getWorkingDirectory();
	}
	public void setWorkingDirectory(File f) {
		shell.setWorkingDirectory(f);
	}
 	protected String absolutePath(String path) {
 		File f = new File(path);
 		if (f.isAbsolute()) {
 			return path;
 		} else {
 			File testPath = new File(getWorkingDirectory(), path);
 			return testPath.getAbsolutePath();
 		}
	}
}