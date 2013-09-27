package ru.fizteh.fivt.students.visamsonov.shell;

public class CommandExit extends CommandAbstract {

	public CommandExit () {
		this.name = "exit";
	}

	public void evaluate (String args) {
		System.exit(0);
	}
}