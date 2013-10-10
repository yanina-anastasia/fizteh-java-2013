package ru.fizteh.fivt.students.mishatkin.shell;

/**
 * MoveCommand.java
 * Created by Vladimir Mishatkin on 9/25/13
 */
public class MoveCommand extends Command {
	MoveCommand(CommandReceiver receiver) {
		super(receiver);
		setInputArgumentsCount(2);
		type = CommandType.MV;
	}

	@Override
	public void execute() throws ShellException {
		receiver.moveCommand(args[0], args[1]);
	}
}
