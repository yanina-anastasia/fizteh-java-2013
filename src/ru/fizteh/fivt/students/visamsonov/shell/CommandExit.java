package ru.fizteh.fivt.students.visamsonov.shell;

public class CommandExit extends CommandAbstract {

	public CommandExit () {
		this.name = "exit";
	}

	public boolean evaluate (ShellState state, String args) {
		System.exit(0);
		return true;
	}
}