package ru.fizteh.fivt.students.inaumov.filemap;

<<<<<<< HEAD
public interface Command<State> {
=======
public interface Command {
>>>>>>> 52e46dc6916f1d8fa0aff1b37a2cd587ef33ceb3
	public String getName();
	
	public int getArgumentsNumber();
	
<<<<<<< HEAD
	public void execute(String[] args, State fileMapState)
=======
	public void execute(String[] args, ShellState fileMapState)
>>>>>>> 52e46dc6916f1d8fa0aff1b37a2cd587ef33ceb3
            throws IllegalArgumentException, UserInterruptionException;
}
