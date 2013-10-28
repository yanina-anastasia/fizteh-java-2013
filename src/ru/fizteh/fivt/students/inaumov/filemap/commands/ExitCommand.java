package ru.fizteh.fivt.students.inaumov.filemap.commands;

import ru.fizteh.fivt.students.inaumov.common.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.filemap.SingleFileMapShellState;
import ru.fizteh.fivt.students.inaumov.common.UserInterruptionException;

public class ExitCommand extends AbstractCommand<SingleFileMapShellState> {
	public ExitCommand() {
		super("exit", 0);
	}

	public void execute(String[] args, SingleFileMapShellState fileMapState) throws UserInterruptionException {
		fileMapState.table.commit();
		throw new UserInterruptionException();
	}
}
