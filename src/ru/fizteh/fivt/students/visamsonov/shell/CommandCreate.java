package ru.fizteh.fivt.students.visamsonov.shell;

public class CommandCreate extends CommandAbstract {

	public CommandCreate () {
		super("create");
	}
	
	public boolean evaluate (ShellState state, String args) {
		if (!checkFixedArguments(splitArguments(args), 1)) {
			return false;
		}
		try {
			state.tableProvider.createTable(args);
		}
		catch (IllegalArgumentException e) {
			getOutStream().println(e.getMessage());
			return false;
		}
		getOutStream().println("created");
		return true;
	}
}