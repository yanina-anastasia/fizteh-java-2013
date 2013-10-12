package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;

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
	
 	protected String absolutePath(String path) throws IOException {
 		File f = new File(path);
 		if (!f.isAbsolute()) {
 			f = new File(getWorkingDirectory(), path);
 		}
 		return f.getCanonicalPath().toString();
	}
}