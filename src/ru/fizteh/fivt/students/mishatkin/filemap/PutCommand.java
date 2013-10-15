package ru.fizteh.fivt.students.mishatkin.filemap;

/**
 * Created by Vladimir Mishatkin on 10/15/13
 */
public class PutCommand extends FileMapCommand {
	public PutCommand(FileMapReceiver receiver) {
		super(receiver);
	}

	@Override
	public int getArgumentsCount() {
		return 2;
	}

	@Override
	public void execute() {
		receiver.putCommand(args[0], args[1]);
	}
}
