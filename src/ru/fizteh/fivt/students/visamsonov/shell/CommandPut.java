package ru.fizteh.fivt.students.visamsonov.shell;

public class CommandPut extends CommandAbstract {

	public CommandPut () {
		super("put");
	}
	
	public boolean evaluate (ShellState state, String args) {
		String[] argArray = splitArguments(args);
		if (!checkFixedArguments(argArray, 2)) {
			return false;
		}
		String oldValue = state.database.get(argArray[0]);
		state.database.put(argArray[0], argArray[1]);
		if (oldValue != null) {
			outStream.printf("overwrite\n%s\n", oldValue);
		}
		else {
			outStream.println("new");
		}
		return true;
	}
}