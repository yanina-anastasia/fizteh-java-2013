package ru.fizteh.fivt.students.visamsonov.shell;

public abstract class CommandAbstract implements Command {

	public String name;

	public void printError (String message) {
		System.err.printf("%s: %s\n", name, message);
	}

	public String getName () {
		return name;
	}
}