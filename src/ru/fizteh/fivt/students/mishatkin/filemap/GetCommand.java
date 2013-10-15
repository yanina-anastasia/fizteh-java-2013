package ru.fizteh.fivt.students.mishatkin.filemap;

/**
 * Created by Vladimir Mishatkin on 10/15/13
 */
public class GetCommand extends  FileMapCommand {
	public GetCommand(FileMapReceiver receiver) {
		super(receiver);
	}

	@Override
	public int getArgumentsCount() {
		return 1;
	}

	@Override
	public void execute() throws TimeToExitException {
		receiver.getCommand(args[0]);
	}
}
