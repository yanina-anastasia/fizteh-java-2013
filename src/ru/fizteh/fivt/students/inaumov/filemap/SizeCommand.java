package ru.fizteh.fivt.students.inaumov.filemap;

<<<<<<< HEAD
public class SizeCommand extends AbstractCommand<SingleFileMapShellState> {
=======
public class SizeCommand extends AbstractCommand {
>>>>>>> 52e46dc6916f1d8fa0aff1b37a2cd587ef33ceb3
	public SizeCommand() {
		super("size", 0);
	}

<<<<<<< HEAD
	public void execute(String[] args, SingleFileMapShellState fileMapState)
=======
	public void execute(String[] args, ShellState fileMapState)
>>>>>>> 52e46dc6916f1d8fa0aff1b37a2cd587ef33ceb3
			throws IllegalArgumentException, UserInterruptionException {
		System.out.println(fileMapState.table.size());
	}
}
