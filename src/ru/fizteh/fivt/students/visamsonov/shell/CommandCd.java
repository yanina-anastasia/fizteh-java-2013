package ru.fizteh.fivt.students.visamsonov.shell;

public class CommandCd extends CommandAbstract {

	public CommandCd () {
		this.name = "cd";
	}

	public boolean evaluate (ShellState state, String args) {
		if (!checkFixedArguments(splitArguments(args), 1)) {
			return false;
		}
		if (!state.setCurrentDirectory(args)) {
			printError(args + ": can't change directory");
			return false;
		}
		return true;
	}
}