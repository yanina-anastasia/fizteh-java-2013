package ru.fizteh.fivt.students.visamsonov.shell;

import java.io.*;

public class CommandExit extends CommandAbstract {

	public CommandExit () {
		this.name = "exit";
	}

	public boolean evaluate (ShellState state, String args) {
		if (!checkFixedArguments(splitArguments(args), 0)) {
			return false;
		}
		try {
			state.database.saveDataToFile();
		}
		catch (IOException e) {
			printError(e.getMessage());
			System.exit(1);
		}
		System.exit(0);
		return true;
	}
}