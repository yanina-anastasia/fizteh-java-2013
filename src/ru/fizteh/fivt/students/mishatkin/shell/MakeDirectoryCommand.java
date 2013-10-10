package ru.fizteh.fivt.students.mishatkin.shell;

/**
 * MakeDirectoryCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class MakeDirectoryCommand extends Command {
	MakeDirectoryCommand(CommandReceiver receiver) {
		super(receiver);
		setInputArgumentsCount(1);
		type = CommandType.MKDIR;
	}

	@Override
	public void execute() throws ShellException {
		receiver.makeDirectoryCommand(args[0]);
	}
}
