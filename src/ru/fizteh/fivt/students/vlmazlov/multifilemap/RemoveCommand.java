package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import java.io.OutputStream;
import java.io.IOException;

public class RemoveCommand extends AbstractDataBaseCommand {
	public RemoveCommand() {
		super("remove", 1);
	}

	public void execute(String[] args, DataBaseState state, OutputStream out) throws CommandFailException {
		if (state.getActiveTable() == null) {
			displayMessage("no table" + SEPARATOR, out);
			return;
		}

		String key = args[0];
		String oldValue = state.getActiveTable().remove(key);

		if (oldValue == null) {
			displayMessage("not found" + SEPARATOR, out);
		} else {
			displayMessage("removed" + SEPARATOR, out);
		}
	}
}