package ru.fizteh.fivt.students.inaumov.filemap;

public class PutCommand extends AbstractCommand {
	public PutCommand() {
		super("put", 2);
	}
	
	public void execute(String[] args, ShellState fileMapState)
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
