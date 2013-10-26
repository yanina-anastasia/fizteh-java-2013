package ru.fizteh.fivt.students.elenav.commands;

import java.io.IOException;
import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.filemap.FileMapState;
import ru.fizteh.fivt.students.elenav.multifilemap.MultiFileMapState;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class CreateTableCommand extends AbstractCommand {

	public CreateTableCommand(FilesystemState s) {
		super(s, "create", 1);
	}

	public void execute(String[] args, PrintStream s) {
		
		if (((MultiFileMapState) getState()).getTables().containsKey(args[1])) {
			getState().getStream().print(args[1]+" exists");
		} else {
			try {
				((MultiFileMapState) getState()).getShell().makeDirectory(args[1]);
				((MultiFileMapState) getState()).getTables().put(args[1], new FileMapState(args[1], getState().getWorkingDirectory(), getState().getStream()));
			} catch (IOException e) {
				getState().getStream().print(e.getMessage());
				System.exit(1);
			}
			getState().getStream().print("created");
		}

	}

}
