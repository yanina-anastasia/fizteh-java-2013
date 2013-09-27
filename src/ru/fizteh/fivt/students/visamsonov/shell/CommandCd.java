package ru.fizteh.fivt.students.visamsonov.shell;

public class CommandCd extends CommandAbstract {

	public CommandCd () {
		this.name = "cd";
	}

	public void evaluate (String args) {
		if (!Utils.setCurrentDirectory(args)) {
			printError(args + ": can't change directory");
		}
	}
}