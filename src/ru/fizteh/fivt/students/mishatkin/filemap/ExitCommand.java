package ru.fizteh.fivt.students.mishatkin.filemap;

import ru.fizteh.fivt.students.mishatkin.shell.ShellCommand;
import ru.fizteh.fivt.students.mishatkin.shell.ShellReceiver;
import ru.fizteh.fivt.students.mishatkin.shell.TimeToExitException;

/**
 * Created by Vladimir Mishatkin on 10/15/13
 */
// deprecated
public class ExitCommand<Receiver extends FileMapReceiver> extends ShellCommand<Receiver> {
	public ExitCommand(ShellReceiver receiver) {
		super((Receiver) receiver);
		setInputArgumentsCount(0);
	}

	@Override
	public void execute() throws TimeToExitException {
		receiver.exitCommand();
	}
}
