package ru.fizteh.fivt.students.visamsonov.shell;

public class CommandPwd extends CommandAbstract {

	public CommandPwd () {
		this.name = "pwd";
	}

	public void evaluate (String args) {
		System.out.println(Utils.getCurrentDirectory());
	}
}