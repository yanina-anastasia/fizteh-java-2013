package ru.fizteh.fivt.students.mishatkin.shell;

/**
 * PwdCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class PwdCommand extends ShellCommand {
	public PwdCommand(ShellReceiver receiver) {
		super(receiver);
		setInputArgumentsCount(0);
	}

	@Override
	public void execute() throws ShellException {
		receiver.printWorkingDirectoryCommand();
	}
}
