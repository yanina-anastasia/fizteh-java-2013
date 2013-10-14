package ru.fizteh.fivt.students.irinapodorozhnaya.db;

import java.io.IOException;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.AbstractCommand;

public class CommandExit extends AbstractCommand{
	public CommandExit(DbState st) {
		super(0, st);
	}

	public String getName() {
		return "exit";
	}

	public void execute(String[] args) throws IOException {
		throw new ExitRuntimeException();
	}
}
