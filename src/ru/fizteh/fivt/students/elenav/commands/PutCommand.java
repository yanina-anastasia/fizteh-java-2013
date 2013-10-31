package ru.fizteh.fivt.students.elenav.commands;

import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.filemap.FileMapState;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;
import ru.fizteh.fivt.students.elenav.states.MonoMultiAbstractState;

public class PutCommand extends AbstractCommand {

	public PutCommand(FilesystemState s) {
		super(s, "put", 2);
	}

	public void execute(String[] args, PrintStream s) {
		MonoMultiAbstractState currentState = (MonoMultiAbstractState) getState();
		FileMapState fileMap = currentState.getWorkingTable();
		if (fileMap == null) {
			getState().getStream().println("no table");
		} else {
			String result = fileMap.map.put(args[1], args[2]);
			if (result != null) {
				getState().getStream().println("overwrite");
				getState().getStream().println(result);
			}
			else {
				if(args[1] == "4key") {
					fileMap.get("1key");
				}
				getState().getStream().println("new");
			}
		}
	}	
}
