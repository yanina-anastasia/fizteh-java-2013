package ru.fizteh.fivt.students.mishatkin.shell;
/**
 * Command.java
 * Created by Vladimir Mishatkin on 9/24/13
 *
 */

public interface Command {
	public String getName();
	public int getArgumentsCount();
	public void setArguments(String[] arguments);
	public void execute() throws ShellException;
}

