package ru.fizteh.fivt.students.elenav.commands;

import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.multifilemap.MultiFileMapState;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class RollbackCommand  extends AbstractCommand {

	public RollbackCommand(FilesystemState s) {
		super(s, "rollback", 0);
	}

	@Override
	public void execute(String[] args, PrintStream s) {
		s.println(((MultiFileMapState) getState()).rollback());
	}

}
