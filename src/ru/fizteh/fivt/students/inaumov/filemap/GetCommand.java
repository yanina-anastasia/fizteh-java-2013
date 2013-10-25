package ru.fizteh.fivt.students.inaumov.filemap;

<<<<<<< HEAD
public class GetCommand extends AbstractCommand<SingleFileMapShellState> {
=======
public class GetCommand extends AbstractCommand {
>>>>>>> 52e46dc6916f1d8fa0aff1b37a2cd587ef33ceb3
	public GetCommand() {
		super("get", 1);
	}
	
<<<<<<< HEAD
	public void execute(String[] args, SingleFileMapShellState fileMapState) throws IllegalArgumentException {
=======
	public void execute(String[] args, ShellState fileMapState) throws IllegalArgumentException {
>>>>>>> 52e46dc6916f1d8fa0aff1b37a2cd587ef33ceb3
		String value = fileMapState.table.get(args[1]);
		if (value == null) {
			System.out.println("not found");
		} else {
			System.out.println("found");
			System.out.println(value);
		}
	}
}
