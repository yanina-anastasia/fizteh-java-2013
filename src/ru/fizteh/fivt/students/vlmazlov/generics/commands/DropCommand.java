package ru.fizteh.fivt.students.vlmazlov.generics.commands;

import java.io.OutputStream;

import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import ru.fizteh.fivt.students.vlmazlov.generics.DataBaseState;

public class DropCommand extends AbstractDataBaseCommand {
	public DropCommand() {
		super("drop", 1);
	}

	public void execute(String[] args, DataBaseState state, OutputStream out) throws CommandFailException {
		String tablename = args[0];

		if (state.getProvider().getTable(tablename) == null) {
			displayMessage(tablename + " not exists" + SEPARATOR, out);
			return;
		}

		if (state.getProvider().getTable(tablename) == state.getActiveTable()) {
			state.setActiveTable(null);
		}

		state.getProvider().removeTable(tablename);

		displayMessage("dropped" + SEPARATOR, out);
	}
}