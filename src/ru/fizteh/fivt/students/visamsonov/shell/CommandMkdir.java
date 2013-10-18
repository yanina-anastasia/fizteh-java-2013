package ru.fizteh.fivt.students.visamsonov.shell;

import java.io.*;

public class CommandMkdir extends CommandAbstract {

	public CommandMkdir () {
		this.name = "mkdir";
	}

	public boolean evaluate (ShellState state, String args) {
		if (!checkFixedArguments(splitArguments(args), 1)) {
			return false;
		}
		if (!(new File(state.getCurrentDirectory(), args).mkdir())) {
			printError("can't create \"" + args + "\"");
			return false;
		}
		return true;
	}
}