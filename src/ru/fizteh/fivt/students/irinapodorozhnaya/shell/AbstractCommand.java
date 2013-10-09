package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

public abstract class AbstractCommand implements Command{
	private int argsNumber;
	private StateShell state;
	public void setNumberOfArguments(int Number) {
		argsNumber = Number;
	}
	public int getNumberOfArguments(){
		return argsNumber;
	}
	public StateShell getState() {
		return state;
	}
	public void setState(StateShell state) {
		this.state = state;
	}
}
