package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import java.io.OutputStream;

public class CommitCommand extends AbstractDataBaseCommand {
	public CommitCommand() {
		super("commit", 0);
	}

	public void execute(String[] args, DataBaseState state, OutputStream out) throws CommandFailException {
		if (state.getActiveTable() == null) {
			displayMessage("no table" + SEPARATOR, out);
			return;
		}

		displayMessage(state.getActiveTable().commit() + SEPARATOR, out);
	}
}