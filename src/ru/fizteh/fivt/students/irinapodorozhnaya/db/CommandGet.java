package ru.fizteh.fivt.students.irinapodorozhnaya.db;

import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.utils.AbstractCommand;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.State;

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
			getState().out.println("not found");
		} else {
			getState().out.println("found " + s);
		}
	}
}
