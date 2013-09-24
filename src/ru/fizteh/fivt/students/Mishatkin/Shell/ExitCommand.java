package ru.fizteh.fivt.students.Mishatkin.Shell;

/**
 * ExitCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class ExitCommand extends Command {
	ExitCommand(ShellReceiver _receiver) {
		super(_receiver);
		type = COMMAND_TYPE.EXIT;
	}

	@Override
	public void execute() throws Exception {
		receiver.exitCommand();
	}
}
