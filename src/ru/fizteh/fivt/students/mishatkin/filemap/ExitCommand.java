package ru.fizteh.fivt.students.mishatkin.filemap;

/**
 * Created by Vladimir Mishatkin on 10/15/13
 */
public class ExitCommand extends FileMapCommand {
	public ExitCommand(FileMapReceiver receiver) {
		super(receiver);
	}

	@Override
	public int getArgumentsCount() {
		return 0;
	}

	@Override
	public void execute() throws TimeToExitException {
		receiver.exitCommand();
	}
}
