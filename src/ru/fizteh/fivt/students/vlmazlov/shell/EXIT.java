package ru.fizteh.fivt.students.vlmazlov.shell;

public class EXIT extends Command {
	EXIT() {
		super("exit", 0);
	};	

	public void execute(String[] args, Shell.ShellState state) throws UserInterruptionException {		
		throw new UserInterruptionException();
	}
}