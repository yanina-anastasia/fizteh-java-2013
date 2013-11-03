package ru.fizteh.fivt.students.mishatkin.shell;

/**
 * MkdirCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class MkdirCommand extends ShellCommand {
	public MkdirCommand(ShellReceiver receiver) {
		super(receiver);
		setInputArgumentsCount(1);
	}

	@Override
	public void execute() throws ShellException {
		receiver.makeDirectoryCommand(args[0]);
	}
}
