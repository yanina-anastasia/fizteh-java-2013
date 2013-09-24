package ru.fizteh.fivt.students.Mishatkin.Shell;

/**
 * PrintWorkingDirectoryCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class PrintWorkingDirectoryCommand extends Command {
	PrintWorkingDirectoryCommand(ShellReceiver _receiver) {
		super(_receiver);
		type = COMMAND_TYPE.PWD;
	}

	@Override
	public void execute() throws Exception {
		receiver.printWorkingDirectoryCommand();
	}
}
