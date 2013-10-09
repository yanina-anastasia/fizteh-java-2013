package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

public abstract class AbstractCommand implements Command{
	protected int argsNumber;
	public int getNumberOfArguments(){
		return argsNumber;
	}
}
