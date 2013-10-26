package ru.fizteh.fivt.students.elenav.commands;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class DropCommand extends AbstractCommand {

	public DropCommand(FilesystemState s) {
		super(s, "drop", 1);
	}

	public void execute(String[] args, PrintStream s) {
		try {
			File table = new File(absolutePath(args[1]));
			if (!table.exists()) {
				getState().getStream().print(args[1] + "not exists");
			} else {
				getState().setWorkingDirectory(table);
				getState().getStream().print("dropped");
			}
		} catch (IOException e) {
			getState().getStream().print(e.getMessage());
			System.exit(1);
		}
	}

}
