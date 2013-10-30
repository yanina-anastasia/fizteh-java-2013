package ru.fizteh.fivt.students.mishatkin.shell;

/**
 * CpCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class CpCommand extends ShellCommand {
	public CpCommand(ShellReceiver receiver) {
		super(receiver);
		setInputArgumentsCount(2);
	}

	@Override
	public void execute() throws ShellException {
		receiver.copyCommand(args[0], args[1]);
	}
}
