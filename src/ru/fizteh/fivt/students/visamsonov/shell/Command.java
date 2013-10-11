package ru.fizteh.fivt.students.visamsonov.shell;

import java.io.PrintStream;

interface Command {

	public void printError (String message);

	public void printError (String message, PrintStream out);

	public String getName ();
	
	public boolean evaluate (ShellState state, String args);
}