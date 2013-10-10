package ru.fizteh.fivt.students.visamsonov.shell;

public class CommandRemove extends CommandAbstract {

	public CommandRemove () {
		this.name = "remove";
	}

	public boolean evaluate (ShellState state, String args) {
		String value = state.database.database.remove(args);
		if (value != null) {
			System.out.printf("removed\n");
		}
		else {
			System.out.printf("not found\n");
		}
		return true;
	}
}