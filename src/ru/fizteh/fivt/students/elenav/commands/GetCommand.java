package ru.fizteh.fivt.students.elenav.commands;

import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.filemap.FileMapState;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;
import ru.fizteh.fivt.students.elenav.states.MonoMultiAbstractState;

public class GetCommand extends AbstractCommand {
	public GetCommand(FilesystemState s) {
		super(s, "get", 1);
	}
	
	public void execute(String[] args, PrintStream s) {
		MonoMultiAbstractState currentState = (MonoMultiAbstractState) getState();
		FileMapState fileMap = currentState.getWorkingTable();
		if (fileMap.equals(null)) {
			getState().getStream().print("no table");
		} else {
			if (fileMap.map.containsKey(args[1])) {
				getState().getStream().println("found");
				getState().getStream().println(((FileMapState) getState()).map.get(args[1]));
			} else {
				getState().getStream().println("not found");
			}
		}
	}
}
