package ru.fizteh.fivt.students.visamsonov.shell;

public class CommandGet extends CommandAbstract {

	public CommandGet () {
		super("get");
	}
	
	public boolean evaluate (ShellState state, String args) {
		if (!checkFixedArguments(splitArguments(args), 1)) {
			return false;
		}
		String value = state.database.get(args);
		if (value != null) {
			getOutStream().printf("found\n%s\n", value);
		}
		else {
			getOutStream().println("not found");
		}
		return true;
	}
}