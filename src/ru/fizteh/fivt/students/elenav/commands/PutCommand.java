package ru.fizteh.fivt.students.elenav.commands;

import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.filemap.FileMapState;
import ru.fizteh.fivt.students.elenav.shell.FilesystemState;

public class PutCommand extends AbstractCommand {

	public PutCommand(FilesystemState s) {
		super(s, "put", 2);
	}

	public void execute(String[] args, PrintStream s) {
		String result = ((FileMapState) getState()).map.put(args[1], args[2]);
		if (result != null) {
			getState().getStream().println("overwrite");
			getState().getStream().println(result);
		}
		else {
			getState().getStream().println("new");
		}
	}	
}
