package ru.fizteh.fivt.students.inaumov.filemap;

public class CommitCommand extends AbstractCommand<SingleFileMapShellState> {
	public CommitCommand() {
		super("commit", 0);
	}

	public void execute(String[] args, SingleFileMapShellState fileMapState) {
		int savedChangesNumber = fileMapState.table.commit();

		System.out.println(savedChangesNumber);
	}
}
