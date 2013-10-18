package ru.fizteh.fivt.students.mishatkin.filemap;

import ru.fizteh.fivt.students.mishatkin.shell.ShellCommand;

/**
 * Created by Vladimir Mishatkin on 10/15/13
 */
public class GetCommand extends ShellCommand {
	public GetCommand(FileMapReceiver receiver) {
		super(receiver);
		setInputArgumentsCount(1);
	}
//	@Override
//	public void execute() throws TimeToExitException {
//		receiver.getCommand(args[0]);
//	}
}
