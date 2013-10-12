package ru.fizteh.fivt.students.mishatkin.shell;

/**
 * ExitCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class ExitCommand extends Command {
	ExitCommand(CommandReceiver receiver) {
		super(receiver);
		setInputArgumentsCount(0);
		type = CommandType.EXIT;
	}

	@Override
	public void execute() throws ShellException {
		receiver.exitCommand();
	}
}
