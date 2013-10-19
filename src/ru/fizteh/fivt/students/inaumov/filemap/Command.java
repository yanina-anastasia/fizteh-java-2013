package ru.fizteh.fivt.students.inaumov.filemap;

public interface Command {
	public String getName();
	
	public int getArgumentsNumber();
	
	public void execute(String[] args, ShellState fileMapState)
            throws IllegalArgumentException, UserInterruptionException;
}
