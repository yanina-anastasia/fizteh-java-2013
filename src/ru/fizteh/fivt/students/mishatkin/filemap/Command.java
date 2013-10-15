package ru.fizteh.fivt.students.mishatkin.filemap;

/**
 * Created by Vladimir Mishatkin on 10/14/13
 */
public interface Command {
	public String getName();
	public int getArgumentsCount();
	public void setArguments(String[] arguments);
	public void execute() throws TimeToExitException;
}
