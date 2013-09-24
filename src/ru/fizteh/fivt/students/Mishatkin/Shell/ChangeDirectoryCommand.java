package ru.fizteh.fivt.students.Mishatkin.Shell;

import java.io.File;

/**
 * ChangeDirectoryCommand.java
 * Created by Vladimir Mishatkin on 9/24/13
 */
public class ChangeDirectoryCommand extends Command {

	ChangeDirectoryCommand(ShellReceiver _receiver) {
		super(_receiver);
		type = COMMAND_TYPE.CD;
	}

	@Override
	public void execute() throws Exception{
		receiver.changeDirectory(args[0]);
	}
}
