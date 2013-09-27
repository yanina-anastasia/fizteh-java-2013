package ru.fizteh.fivt.students.visamsonov.shell;

public final class RawCommand {
	
	public String name;
	public String args;

	public RawCommand (String commandName, String commandArgs) {
		this.name = commandName;
		this.args = commandArgs;
	}
}