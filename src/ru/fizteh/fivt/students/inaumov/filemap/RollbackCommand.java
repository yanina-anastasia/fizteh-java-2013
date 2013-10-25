package ru.fizteh.fivt.students.inaumov.filemap;

<<<<<<< HEAD
public class RollbackCommand extends AbstractCommand<SingleFileMapShellState> {
=======
public class RollbackCommand extends AbstractCommand {
>>>>>>> 52e46dc6916f1d8fa0aff1b37a2cd587ef33ceb3
	public RollbackCommand() {
		super("rollback", 0);
	}

<<<<<<< HEAD
	public void execute(String[] args, SingleFileMapShellState fileMapState) {
=======
	public void execute(String[] args, ShellState fileMapState) {
>>>>>>> 52e46dc6916f1d8fa0aff1b37a2cd587ef33ceb3
		int unsavedChangesNumber = fileMapState.table.rollback();
		System.out.println(unsavedChangesNumber);
	}
}
