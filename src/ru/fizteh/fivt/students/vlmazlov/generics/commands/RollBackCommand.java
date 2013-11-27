package ru.fizteh.fivt.students.vlmazlov.generics.commands;

import java.io.OutputStream;

import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import ru.fizteh.fivt.students.vlmazlov.generics.DataBaseState;

public class RollBackCommand extends AbstractDataBaseCommand {
	public RollBackCommand() {
		super("rollback", 0);
	}

	public void execute(String[] args, DataBaseState state, OutputStream out) throws CommandFailException {
		if (state.getActiveTable() == null) {
			displayMessage("no table" + SEPARATOR, out);
			return;
		}

		displayMessage(state.getActiveTable().rollback() + SEPARATOR, out);
	}
}