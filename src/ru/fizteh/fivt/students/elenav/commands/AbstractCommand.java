package ru.fizteh.fivt.students.elenav.commands;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.elenav.shell.State;

public abstract class AbstractCommand implements Command {
	private final State state;
	private final String name;
	private final int argNumber;
	
	AbstractCommand(State s, String nm, int n) {
		state = s;
		name = nm;
		argNumber = n;
	}
	
	public String getName() {
		return name;
	}
	
	public int getArgNumber() {
		return argNumber;
	}

	public State getState() {
		return state;
	}
	
 	protected String absolutePath(String path) throws IOException {
 		File f = new File(path);
 		if (!f.isAbsolute()) {
 			f = new File(state.getWorkingDirectory(), path);
 		}
 		return f.getCanonicalPath().toString();
	}

}