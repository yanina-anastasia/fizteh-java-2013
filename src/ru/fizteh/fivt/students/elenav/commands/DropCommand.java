package ru.fizteh.fivt.students.elenav.commands;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.multifilemap.MultiFileMapState;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class DropCommand extends AbstractCommand {

	public DropCommand(FilesystemState s) {
		super(s, "drop", 1);
	}

	public void execute(String[] args, PrintStream s) {
		try {
			File table = new File(getState().getWorkingDirectory(), args[1]);
			if (!table.exists()) {
				getState().getStream().println(args[1] + " not exists");
			} else {
				MultiFileMapState multi = (MultiFileMapState) getState();
				if (multi.getWorkingTable().getName().equals(table.getName())) {
					multi.setWorkingTable(null);
				}
				multi.getShell().rm(table.getCanonicalPath());
				getState().getStream().println("dropped");
			}
		} catch (IOException e) {
			getState().getStream().println(e.getMessage());
			System.exit(1);
		}
	}

}
