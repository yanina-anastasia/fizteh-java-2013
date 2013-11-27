package ru.fizteh.fivt.students.vlmazlov.generics.commands;

import java.io.OutputStream;
import java.io.IOException;

import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import ru.fizteh.fivt.students.vlmazlov.generics.DataBaseState;

public class CommitCommand extends AbstractDataBaseCommand {
	public CommitCommand() {
		super("commit", 0);
	}

	public void execute(String[] args, DataBaseState state, OutputStream out) throws CommandFailException {
		if (state.getActiveTable() == null) {
			displayMessage("no table" + SEPARATOR, out);
			return;
		}
		try {
			displayMessage(state.getActiveTable().commit() + SEPARATOR, out);
		} catch (IOException ex) {
			throw new CommandFailException("Commit failed: " + ex.getMessage());
		}
	}
}