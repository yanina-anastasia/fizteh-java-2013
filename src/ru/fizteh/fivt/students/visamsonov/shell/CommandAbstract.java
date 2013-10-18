package ru.fizteh.fivt.students.visamsonov.shell;

import java.io.PrintStream;

public abstract class CommandAbstract implements Command {

	protected String name;

	public void printError (String message, PrintStream out) {
		out.printf("%s: %s\n", name, message);
	}

	public void printError (String message) {
		printError(message, System.err);
	}

	protected String[] splitArguments (String args) {
		if (args == null || args.equals("")) {
			return new String[0];
		}
		return args.split("\\s+");
	}

	protected boolean checkFixedArguments (String[] args, int number) {
		if (args == null || args.length != number) {
			printError("given " + args.length + " arguments, expected " + number);
			return false;
		}
		return true;
	}

	public String getName () {
		return name;
	}
}