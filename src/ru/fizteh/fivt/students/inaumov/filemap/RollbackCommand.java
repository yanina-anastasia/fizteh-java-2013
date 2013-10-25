package ru.fizteh.fivt.students.inaumov.filemap;

public class RollbackCommand extends AbstractCommand<SingleFileMapShellState> {
	public RollbackCommand() {
		super("rollback", 0);
	}

	public void execute(String[] args, SingleFileMapShellState fileMapState) {
		int unsavedChangesNumber = fileMapState.table.rollback();

		System.out.println(unsavedChangesNumber);
	}
}
