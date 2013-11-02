package ru.fizteh.fivt.students.elenav.commands;

import java.io.IOException;
import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.multifilemap.MultiFileMapState;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class CommitCommand extends AbstractCommand {

	public CommitCommand(FilesystemState s) {
		super(s, "commit", 0);
	}

	@Override
	public void execute(String[] args, PrintStream s) throws IOException {
		MultiFileMapState multi = (MultiFileMapState) getState();
		if (multi.getWorkingTable() == null) {
			getState().getStream().println("no table");
		} else {
			getState().getStream().println(multi.commit());
		}
		
	}
	

}
