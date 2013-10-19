package ru.fizteh.fivt.students.inaumov.filemap;

public class SizeCommand extends AbstractCommand {
	public SizeCommand() {
		super("size", 0);
	}

	public void execute(String[] args, ShellState fileMapState)
			throws IllegalArgumentException, UserInterruptionException {
		System.out.println(fileMapState.table.size());
	}
}
