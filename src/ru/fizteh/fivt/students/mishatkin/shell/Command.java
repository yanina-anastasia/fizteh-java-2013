package ru.fizteh.fivt.students.mishatkin.shell;
/**
 * Command.java
 * Created by Vladimir Mishatkin on 9/24/13
 *
 */

public abstract class Command {
	private static int inputArgumentsCount;
	protected CommandType type;
	protected String[] args = new String[2];
	protected CommandReceiver receiver;

	public CommandType getType() {
		return type;
	}

	public int getInputArgumentsCount() {
		return inputArgumentsCount;
	}

	protected static void setInputArgumentsCount(int inputArgumentsCount) {
		Command.inputArgumentsCount = inputArgumentsCount;
	}

	Command(CommandReceiver receiver) {
		this.receiver = receiver;
	}

	public abstract void execute() throws ShellException;

}
