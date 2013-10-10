package ru.fizteh.fivt.students.mishatkin.shell;

/**
 * DirectoryCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class DirectoryCommand extends Command {
	DirectoryCommand(CommandReceiver receiver) {
		super(receiver);
		setInputArgumentsCount(0);
		type = CommandType.DIR;
	}

	@Override
	public void execute() throws ShellException {
		receiver.directoryCommand();
	}

}
