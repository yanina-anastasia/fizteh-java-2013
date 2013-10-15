package ru.fizteh.fivt.students.mishatkin.filemap;

/**
 * Created by Vladimir Mishatkin on 10/15/13
 */
public class RemoveCommand extends FileMapCommand {
	public RemoveCommand(FileMapReceiver receiver) {
		super(receiver);
	}

	@Override
	public int getArgumentsCount() {
		return 1;
	}

	@Override
	public void execute() throws TimeToExitException {
		receiver.removeCommand(args[0]);
	}
}
