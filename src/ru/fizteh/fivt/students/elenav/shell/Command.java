package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public interface Command {
	public String getName();
	public int getArgNumber();
	public void setName(String name);
	public void setArgNumber(int number);
	public void setShell(ShellState s);
	public File getWorkingDirectory();
	public void setWorkingDirectory(File f);
	public void execute(String args[], PrintStream s) throws IOException;
}
