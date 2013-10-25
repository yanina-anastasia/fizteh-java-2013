package ru.fizteh.fivt.students.inaumov.filemap;

<<<<<<< HEAD
public class ExitCommand extends AbstractCommand<SingleFileMapShellState> {
=======
public class ExitCommand extends AbstractCommand {
>>>>>>> 52e46dc6916f1d8fa0aff1b37a2cd587ef33ceb3
	public ExitCommand() {
		super("exit", 0);
	}
	
<<<<<<< HEAD
	public void execute(String[] args, SingleFileMapShellState fileMapState)
=======
	public void execute(String[] args, ShellState fileMapState)
>>>>>>> 52e46dc6916f1d8fa0aff1b37a2cd587ef33ceb3
			throws UserInterruptionException, IllegalArgumentException {
		fileMapState.table.commit();
		throw new UserInterruptionException();
	}
}
