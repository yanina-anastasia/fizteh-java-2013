package ru.fizteh.fivt.students.mishatkin.shell;

/**
 * PrintWorkingDirectoryCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class PrintWorkingDirectoryCommand extends Command {
	PrintWorkingDirectoryCommand(CommandReceiver receiver) {
		super(receiver);
		type = COMMAND_TYPE.PWD;
	}

	@Override
	public void execute() throws ShellException {
		receiver.printWorkingDirectoryCommand();
	}
}
