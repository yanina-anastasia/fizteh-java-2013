package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;

public abstract class AbstractCommand implements Command {
	private final int argsNumber;
	private StateShell state;
	
	public AbstractCommand(int argsNumber, StateShell state) {
		this.argsNumber =argsNumber;
		this.state = state;
	}
	
	public int getNumberOfArguments() {
		return argsNumber;
	}
	
	public StateShell getState() {
		return state;
	}
	
	public void setState(StateShell state) {
		this.state = state;
	}
	
	protected File getFileByName(String path) {
		File f = new File(path);
		if (f.isAbsolute()) {
			return f;
		} else {
			return new File (state.currentDir, path);
		}
	}
}
