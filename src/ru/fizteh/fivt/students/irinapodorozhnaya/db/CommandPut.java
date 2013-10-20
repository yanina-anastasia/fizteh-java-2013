package ru.fizteh.fivt.students.irinapodorozhnaya.db;

import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.shell.AbstractCommand;

public class CommandPut extends AbstractCommand {
	public CommandPut(DbState state) {
		super (2, state);
	}

	public String getName() {
		return "put";
	}

	public void execute(String[] args) throws IOException {
		String s = ((DbState) getState()).getData().put(args[1], args[2]);
		if (s != null) {
			getState().getOutputStream().println("overwrite " + s);
		} else {
			getState().getOutputStream().println("new");
		}
	}
}
