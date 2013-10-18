package ru.fizteh.fivt.students.elenav.commands;

import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.filemap.FileMapState;
import ru.fizteh.fivt.students.elenav.shell.FilesystemState;

public class RemoveCommand extends AbstractCommand {

	public RemoveCommand(FilesystemState s) {
		super(s, "remove", 1);
	}

	public void execute(String[] args, PrintStream s) {
		if (((FileMapState) getState()).map.containsKey(args[1])) {
			((FileMapState) getState()).map.remove(args[1]);
			getState().getStream().println("removed");
		} else {
			getState().getStream().println("not found");
		}
		
	}

}
