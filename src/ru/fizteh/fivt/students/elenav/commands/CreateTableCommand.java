package ru.fizteh.fivt.students.elenav.commands;

import java.io.IOException;
import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.multifilemap.MultiFileMapState;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class CreateTableCommand extends AbstractCommand {

	public CreateTableCommand(FilesystemState s) {
		super(s, "create", 1);
	}

	public void execute(String[] args, PrintStream s) {
		try {
			MultiFileMapState multi = (MultiFileMapState) getState();
			multi.getShell().makeDirectory(args[1]);
			getState().getStream().println("created");
		} catch (IOException e) {
			getState().getStream().println(args[1]+" exists");
		}
	}

}
