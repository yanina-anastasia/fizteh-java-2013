package ru.fizteh.fivt.students.inaumov.filemap;

public class ExitCommand extends AbstractCommand {
	public ExitCommand() {
		super("exit", 0);
	}
	
	public void execute(String[] args, FileMapState fileMapState) 
			throws UserInterruptionException, IncorrectArgumentsException {
		fileMapState.table.commit();
		throw new UserInterruptionException();
	}
}
