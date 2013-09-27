package ru.fizteh.fivt.students.visamsonov.shell;

interface Command {

	public void printError (String message);

	public String getName ();
	
	public void evaluate (String args);
}