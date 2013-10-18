package ru.fizteh.fivt.students.visamsonov.shell;

public class CommandPwd extends CommandAbstract {

	public CommandPwd () {
		this.name = "pwd";
	}

	public boolean evaluate (ShellState state, String args) {
		if (!checkFixedArguments(splitArguments(args), 0)) {
			return false;
		}
		System.out.println(state.getCurrentDirectory());
		return true;
	}
}