package ru.fizteh.fivt.students.elenav.commands;

import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.filemap.FileMapState;
import ru.fizteh.fivt.students.elenav.states.MonoMultiAbstractState;

public class RemoveCommand extends AbstractCommand {

	public RemoveCommand(MonoMultiAbstractState s) {
		super(s, "remove", 1);
	}

	public void execute(String[] args, PrintStream s) {
		MonoMultiAbstractState currentState = (MonoMultiAbstractState) getState();
		FileMapState fileMap = currentState.getWorkingTable();
		if (fileMap.equals(null)) {
			getState().getStream().print("no table");
		} else {
			if (fileMap.map.containsKey(args[1])) {
				fileMap.map.remove(args[1]);
				getState().getStream().println("removed");
			} else {
				getState().getStream().println("not found");
			}
		
		}
	}
	
}
