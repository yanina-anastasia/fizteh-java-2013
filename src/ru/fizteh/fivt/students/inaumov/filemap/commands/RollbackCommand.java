package ru.fizteh.fivt.students.inaumov.filemap.commands;

import ru.fizteh.fivt.students.inaumov.common.AbstractCommand;
import ru.fizteh.fivt.students.inaumov.filemap.SingleFileMapShellState;

public class RollbackCommand extends AbstractCommand<SingleFileMapShellState> {
	public RollbackCommand() {
		super("rollback", 0);
	}

	public void execute(String[] args, SingleFileMapShellState fileMapState) {
		int unsavedChangesNumber = fileMapState.table.rollback();

		System.out.println(unsavedChangesNumber);
	}
}
