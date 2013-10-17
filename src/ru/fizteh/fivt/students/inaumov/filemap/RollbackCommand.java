package ru.fizteh.fivt.students.inaumov.filemap;

public class RollbackCommand extends AbstractCommand {
	public RollbackCommand() {
		super("rollback", 0);
	}

	public void execute(String[] args, FileMapState fileMapState) {
		int unsavedChangesNumber = fileMapState.table.rollback();
		System.out.println(unsavedChangesNumber);
	}
}
