package ru.fizteh.fivt.students.mishatkin.filemap;

import ru.fizteh.fivt.students.mishatkin.shell.ShellCommand;
import ru.fizteh.fivt.students.mishatkin.shell.ShellReceiver;
import ru.fizteh.fivt.students.mishatkin.shell.TimeToExitException;

/**
 * Created by Vladimir Mishatkin on 10/15/13
 */
public class ExitCommand extends ShellCommand {
	public ExitCommand(ShellReceiver receiver) {
		super(receiver);
		setInputArgumentsCount(0);
	}

	@Override
	public void execute() throws TimeToExitException {
		receiver.exitCommand();
	}
}
