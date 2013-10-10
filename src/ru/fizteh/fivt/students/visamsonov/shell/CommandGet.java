package ru.fizteh.fivt.students.visamsonov.shell;

public class CommandGet extends CommandAbstract {

	public CommandGet () {
		this.name = "get";
	}

	public boolean evaluate (ShellState state, String args) {
		String value = state.database.database.get(args);
		if (value != null) {
			System.out.printf("found\n%s\n", value);
		}
		else {
			System.out.printf("not found\n");
		}
		return true;
	}
}