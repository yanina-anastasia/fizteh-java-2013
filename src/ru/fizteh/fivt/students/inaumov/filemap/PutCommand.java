package ru.fizteh.fivt.students.inaumov.filemap;

<<<<<<< HEAD
public class PutCommand extends AbstractCommand<SingleFileMapShellState> {
=======
public class PutCommand extends AbstractCommand {
>>>>>>> 52e46dc6916f1d8fa0aff1b37a2cd587ef33ceb3
	public PutCommand() {
		super("put", 2);
	}
	
<<<<<<< HEAD
	public void execute(String[] args, SingleFileMapShellState fileMapState)
=======
	public void execute(String[] args, ShellState fileMapState)
>>>>>>> 52e46dc6916f1d8fa0aff1b37a2cd587ef33ceb3
			throws IllegalArgumentException {
		String oldValue = fileMapState.table.put(args[1], args[2]);
		
		if (oldValue == null) {
			System.out.println("new");
		} else {
			System.out.println("overwrite");
			System.out.println(oldValue);
		}
	}
}
