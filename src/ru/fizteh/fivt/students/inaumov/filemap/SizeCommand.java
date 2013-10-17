package ru.fizteh.fivt.students.inaumov.filemap;

public class SizeCommand extends AbstractCommand {
	public SizeCommand() {
		super("size", 0);
	}

	public void execute(String[] args, FileMapState fileMapState) 
			throws IncorrectArgumentsException, UserInterruptionException {
		System.out.println(fileMapState.table.size());
	}
}
