package ru.fizteh.fivt.students.Mishatkin.Shell;

/**
 * MakeDirectoryCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class MakeDirectoryCommand extends Command {
	MakeDirectoryCommand(ShellReceiver _receiver) {
		super(_receiver);
		type = COMMAND_TYPE.MKDIR;
	}

	@Override
	public void execute() throws Exception {
		receiver.makeDirectoryCommand(args[0]);
	}
}
