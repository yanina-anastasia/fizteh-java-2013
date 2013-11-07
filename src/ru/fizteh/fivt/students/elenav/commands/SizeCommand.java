package ru.fizteh.fivt.students.elenav.commands;

import java.io.IOException;
import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.multifilemap.MultiFileMapState;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class SizeCommand extends AbstractCommand {

	public SizeCommand(FilesystemState s) {
		super(s, "size", 0);
	}

	@Override
	public void execute(String[] args, PrintStream s) throws IOException {
		MultiFileMapState multi = (MultiFileMapState) getState();
		if (multi.getWorkingDirectory() != null) {
			s.println(multi.size());
		}
		
	}

}
