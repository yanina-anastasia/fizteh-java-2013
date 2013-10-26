package ru.fizteh.fivt.students.mishatkin.multifilehashmap;

import ru.fizteh.fivt.students.mishatkin.shell.ShellCommand;
import ru.fizteh.fivt.students.mishatkin.shell.ShellReceiver;

/**
 * Created by Vladimir Mishatkin on 10/26/13
 */
public class CreateCommand extends ShellCommand {
	public CreateCommand(MultiFileHashMapReceiver receiver) {
		super(receiver);
		setInputArgumentsCount(1);
	}
}
