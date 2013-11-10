package ru.fizteh.fivt.students.elenav.commands;

import java.io.PrintStream;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.elenav.multifilemap.MultiFileMapState;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class CreateTableCommand extends AbstractCommand {

	public CreateTableCommand(FilesystemState s) {
		super(s, "create", 1);
	}

	public void execute(String[] args, PrintStream s) {
		MultiFileMapState multi = (MultiFileMapState) getState();
		Table result = multi.provider.createTable(args[1]);
		if (result != null) {
			s.println("created");
		} else { 
		s.println(args[1]+" exists");
		}
	}
}
