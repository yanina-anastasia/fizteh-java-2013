package ru.fizteh.fivt.students.mishatkin.shell;

/**
 * ChangeDirectoryCommand.java
 * Created by Vladimir Mishatkin on 9/24/13
 */
public class ChangeDirectoryCommand extends Command {

	ChangeDirectoryCommand(CommandReceiver receiver) {
		super(receiver);
		setInputArgumentsCount(1);
		type = CommandType.CD;
	}

	@Override
	public void execute() throws ShellException{
		receiver.changeDirectoryCommand(args[0]);
	}
}
