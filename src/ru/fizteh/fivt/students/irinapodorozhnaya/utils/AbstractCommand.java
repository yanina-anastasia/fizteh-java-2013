package ru.fizteh.fivt.students.irinapodorozhnaya.utils;

import java.io.File;

public abstract class AbstractCommand implements Command {
	private final int argsNumber;
	private final State state;
	
	public AbstractCommand(int argsNumber, State state) {
		this.argsNumber = argsNumber;
		this.state = state;
	}
	
	public int getNumberOfArguments() {
		return argsNumber;
	}
	
	public State getState() {
		return state;
	}
	
	protected File getFileByName(String path) {
		File f = new File(path);
		if (f.isAbsolute()) {
			return f;
		} else {
			return new File (getState().currentDir, path);
		}
	}
}
