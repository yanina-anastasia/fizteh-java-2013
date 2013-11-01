package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import java.io.OutputStream;

public class GetCommand extends AbstractDataBaseCommand {
	public GetCommand() {
		super("get", 1);
	}

	public void execute(String[] args, DataBaseState state, OutputStream out) throws CommandFailException {
		if (state.getActiveTable() == null) {
			displayMessage("no table" + SEPARATOR, out);
			return;
		}

		String key = args[0];
		String value = state.getActiveTable().get(key);

		if (value == null) {
			displayMessage("not found" + SEPARATOR, out);
		} else {
			displayMessage("found" + SEPARATOR + value + SEPARATOR, out);
		}
	}
}