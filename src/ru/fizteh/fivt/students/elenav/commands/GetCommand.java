package ru.fizteh.fivt.students.elenav.commands;

import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.filemap.FileMapState;
import ru.fizteh.fivt.students.elenav.shell.State;

public class GetCommand extends AbstractCommand {
	public GetCommand(State s) {
		super(s, "get", 1);
	}
	
	public void execute(String[] args, PrintStream s) {
		if (((FileMapState) getState()).map.containsKey(args[1])) {
			getState().getStream().println("found");
			getState().getStream().println(((FileMapState) getState()).map.get(args[1]));
		}
		else {
			getState().getStream().println("not found");
		}
	}
}
