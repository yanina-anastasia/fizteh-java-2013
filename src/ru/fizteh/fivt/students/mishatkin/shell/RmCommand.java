package ru.fizteh.fivt.students.mishatkin.shell;
/**
 * RmCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class RmCommand extends ShellCommand {
	public RmCommand(ShellReceiver receiver) {
		super(receiver);
		setInputArgumentsCount(1);
	}

	@Override
	public void execute() throws ShellException {
		receiver.removeCommand(args[0]);
	}
}
