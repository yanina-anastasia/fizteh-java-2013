package ru.fizteh.fivt.students.inaumov.filemap;

public interface Command<State> {
	public String getName();
	
	public int getArgumentsNumber();

	public void execute(String[] args, State fileMapState) throws UserInterruptionException;
}
