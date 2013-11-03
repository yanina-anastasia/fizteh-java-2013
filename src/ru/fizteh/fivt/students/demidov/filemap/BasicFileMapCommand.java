package ru.fizteh.fivt.students.demidov.filemap;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.shell.BasicCommand;
import ru.fizteh.fivt.students.demidov.shell.Shell;
import ru.fizteh.fivt.students.demidov.shell.ShellInterruptionException;

public abstract class BasicFileMapCommand implements BasicCommand {
	public BasicFileMapCommand(BasicState currentState) {
		this.currentState = currentState;
	}
	
	abstract public void executeCommand(String[] arguments, Shell usedShell) throws IOException, ShellInterruptionException;
	abstract public int getNumberOfArguments();
	abstract public String getCommandName();
	
	protected BasicState currentState;
}
