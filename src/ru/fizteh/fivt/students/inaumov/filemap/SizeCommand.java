package ru.fizteh.fivt.students.inaumov.filemap;

public class SizeCommand extends AbstractCommand<SingleFileMapShellState> {
	public SizeCommand() {
		super("size", 0);
	}

	public void execute(String[] args, SingleFileMapShellState fileMapState) {
		System.out.println(fileMapState.table.size());
	}
}
