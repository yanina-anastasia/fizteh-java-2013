package ru.fizteh.fivt.students.inaumov.filemap;

public class GetCommand extends AbstractCommand {
	public GetCommand() {
		super("get", 1);
	}
	
	public void execute(String[] args, ShellState fileMapState) throws IllegalArgumentException {
		String value = fileMapState.table.get(args[1]);
		if (value == null) {
			System.out.println("not found");
		} else {
			System.out.println("found");
			System.out.println(value);
		}
	}
}
