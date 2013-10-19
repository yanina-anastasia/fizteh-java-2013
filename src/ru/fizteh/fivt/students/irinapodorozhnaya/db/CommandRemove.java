package ru.fizteh.fivt.students.irinapodorozhnaya.db;

import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.shell.AbstractCommand;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.State;

public class CommandRemove extends AbstractCommand {

	public CommandRemove(State state) {
		super(1, state);
	}

	public String getName() {
		return "remove";
	}

	public void execute(String[] args) throws IOException {
		String s = ((DbState) getState()).getData().remove(args[1]);
		if (s == null) {
			getState().getOutputStream().println("not found");
		} else {
			getState().getOutputStream().println("removed");
		}
	}
}
