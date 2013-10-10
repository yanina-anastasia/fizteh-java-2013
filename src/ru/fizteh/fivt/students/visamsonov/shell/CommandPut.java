package ru.fizteh.fivt.students.visamsonov.shell;

public class CommandPut extends CommandAbstract {

	public CommandPut () {
		this.name = "put";
	}

	public boolean evaluate (ShellState state, String args) {
		String[] argArray = args.split("\\s+");
		if (argArray.length != 2) {
			printError("given " + argArray.length + " arguments, expected 2");
			return false;
		}
		String oldValue = state.database.database.get(argArray[0]);
		state.database.database.put(argArray[0], argArray[1]);
		if (oldValue != null) {
			System.out.printf("overwrite\n%s\n", oldValue);
		}
		else {
			System.out.printf("new\n");
		}
		return true;
	}
}