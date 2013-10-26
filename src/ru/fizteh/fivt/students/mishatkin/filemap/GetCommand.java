package ru.fizteh.fivt.students.mishatkin.filemap;

import ru.fizteh.fivt.students.mishatkin.shell.ShellCommand;
import ru.fizteh.fivt.students.mishatkin.shell.ShellReceiver;

/**
 * Created by Vladimir Mishatkin on 10/15/13
 */
public class GetCommand extends ShellCommand {
	public GetCommand(ShellReceiver receiver) {
		super(receiver);
		setInputArgumentsCount(1);
	}
//	@Override
//	public void execute() throws TimeToExitException {
//		receiver.getCommand(args[0]);
//	}
}
