package ru.fizteh.fivt.students.visamsonov.shell;

public class CommandUse extends CommandAbstract {

	public CommandUse () {
		super("use");
	}
	
	public boolean evaluate (ShellState state, String args) {
		if (!checkFixedArguments(splitArguments(args), 1)) {
			return false;
		}
		try {
			if (state.database != null) {
				state.database.commit();
			}
			state.database = state.tableProvider.getTable(args);
			if (state.database == null) {
				printError("\"" + args + "\" not exists");
				return false;
			}
		}
		catch (IllegalArgumentException e) {
			printError(e.getMessage());
			return false;
		}
		getOutStream().printf("using %s\n", args);
		return true;
	}
}