package ru.fizteh.fivt.students.mishatkin.shell;
/**
 * Command.java
 * Created by Vladimir Mishatkin on 9/24/13
 *
 */

public abstract class Command {

	protected CommandType type;
	protected String[] args = new String[2];
	protected CommandReceiver receiver;

	public CommandType getType() {
		return type;
	}

	Command(CommandReceiver receiver) {
		this.receiver = receiver;
	}

	public abstract void execute() throws ShellException;

}
