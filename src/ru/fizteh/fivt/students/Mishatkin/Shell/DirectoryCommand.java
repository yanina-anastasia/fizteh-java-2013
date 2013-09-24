package ru.fizteh.fivt.students.Mishatkin.Shell;

/**
 * DirectoryCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class DirectoryCommand extends Command {
	DirectoryCommand(ShellReceiver _receiver) {
		super(_receiver);
		type = COMMAND_TYPE.DIR;
	}

	@Override
	public void execute() throws Exception {
		receiver.directoryCommand();
	}

}
