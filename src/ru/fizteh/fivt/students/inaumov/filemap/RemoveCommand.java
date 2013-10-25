package ru.fizteh.fivt.students.inaumov.filemap;

public class RemoveCommand extends AbstractCommand<SingleFileMapShellState> {
	public RemoveCommand() {
		super("remove", 1);
	}
	
	public void execute(String[] args, SingleFileMapShellState fileMapState)
			throws IllegalArgumentException {
		String oldValue = fileMapState.table.remove(args[1]);
		if (oldValue == null) {
			System.out.println("not found");
		} else {
			System.out.println("removed");
		}
	}
}
