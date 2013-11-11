package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import java.io.OutputStream;
import java.io.IOException;

public class PutCommand extends AbstractDataBaseCommand {
	public PutCommand() {
		super("put", 2);
	}

	public void execute(String[] args, DataBaseState state, OutputStream out) throws CommandFailException {
		if (state.getActiveTable() == null) {
			displayMessage("no table" + SEPARATOR, out);
			return;
		}

		String key = args[0], value = args[1];
		String oldValue = state.getActiveTable().put(key, value);

		if (oldValue == null) {
			displayMessage("new" + SEPARATOR, out);
		} else {
			displayMessage("overwrite" + SEPARATOR + oldValue + SEPARATOR, out);
		}
	}
}