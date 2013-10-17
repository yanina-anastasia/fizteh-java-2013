package ru.fizteh.fivt.students.irinapodorozhnaya.db;

import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.shell.AbstractCommand;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.State;

public class CommandGet extends AbstractCommand {

	public CommandGet(State state) {
		super(1, state);
	}

	public String getName() {
		return "get";
	}

	public void execute(String[] args) throws IOException {
		String s = ((DbState) getState()).getData().get(args[1]);
		if (s == null) {
			getState().getOutputStream().println("not found");
		} else {
			getState().getOutputStream().println("found " + s);
		}
	}
}
