package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

public abstract class AbstractCommand implements Command{
	private int argsNumber;
	public void setNumberOfArguments(int Number) {
		argsNumber = Number;
	}
	public int getNumberOfArguments(){
		return argsNumber;
	}
}
