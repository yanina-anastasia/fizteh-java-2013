package ru.fizteh.fivt.students.inaumov.filemap;

public class RemoveCommand extends AbstractCommand {
	public RemoveCommand() {
		super("remove", 1);
	}
	
	public void execute(String[] args, FileMapState fileMapState) 
			throws IncorrectArgumentsException {
		String oldValue = fileMapState.table.remove(args[1]);
		if (oldValue == null) {
			System.out.println("not found");
		} else {
			System.out.println("removed");
		}
	}
}
