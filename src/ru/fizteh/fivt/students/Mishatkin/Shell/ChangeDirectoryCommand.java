package ru.fizteh.fivt.students.Mishatkin.Shell;

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
		receiver.changeDirectoryCommand(args[0]);
	}
}
