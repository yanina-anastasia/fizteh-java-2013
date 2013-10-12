package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public interface Command {
	String getName();
	int getArgNumber();
	File getWorkingDirectory();
	void setWorkingDirectory(File f);
	void execute(String args[], PrintStream s) throws IOException;
}
