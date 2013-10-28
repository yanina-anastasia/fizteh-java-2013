package ru.fizteh.fivt.students.inaumov.common;

public interface Command<State> {
	public String getName();
	
	public int getArgumentsNumber();

	public void execute(String[] args, State fileMapState) throws UserInterruptionException;
}
