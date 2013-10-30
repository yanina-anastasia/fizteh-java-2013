package ru.fizteh.fivt.students.mishatkin.shell;

/**
 * ExitCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class ExitCommand extends ShellCommand {
	public ExitCommand(ShellReceiver receiver) {
		super(receiver);
		setInputArgumentsCount(0);
	}

	@Override
	public void execute() throws ShellException {
		receiver.exitCommand();
	}
}
