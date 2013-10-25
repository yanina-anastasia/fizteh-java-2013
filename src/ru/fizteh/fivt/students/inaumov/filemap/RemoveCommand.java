package ru.fizteh.fivt.students.inaumov.filemap;

<<<<<<< HEAD
public class RemoveCommand extends AbstractCommand<SingleFileMapShellState> {
=======
public class RemoveCommand extends AbstractCommand {
>>>>>>> 52e46dc6916f1d8fa0aff1b37a2cd587ef33ceb3
	public RemoveCommand() {
		super("remove", 1);
	}
	
<<<<<<< HEAD
	public void execute(String[] args, SingleFileMapShellState fileMapState)
=======
	public void execute(String[] args, ShellState fileMapState)
>>>>>>> 52e46dc6916f1d8fa0aff1b37a2cd587ef33ceb3
			throws IllegalArgumentException {
		String oldValue = fileMapState.table.remove(args[1]);
		if (oldValue == null) {
			System.out.println("not found");
		} else {
			System.out.println("removed");
		}
	}
}
