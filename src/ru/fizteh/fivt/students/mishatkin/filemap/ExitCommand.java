package ru.fizteh.fivt.students.mishatkin.filemap;

import ru.fizteh.fivt.students.mishatkin.shell.ShellCommand;

/**
 * Created by Vladimir Mishatkin on 10/15/13
 */
public class ExitCommand extends ShellCommand {
	public ExitCommand(FileMapReceiver receiver) {
		super(receiver);
		setInputArgumentsCount(0);
	}

//	@Override
//	public void execute() throws TimeToExitException {
//		receiver.exitCommand();
//	}
}
