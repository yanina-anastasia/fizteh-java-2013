package ru.fizteh.fivt.students.vlmazlov.filemap;

import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import java.io.OutputStream;
import java.io.IOException;

public class RemoveCommand extends AbstractFileMapCommand {
	public RemoveCommand() {
		super("remove", 1);
	}

	public void execute(String[] args, FileMap state, OutputStream out) throws CommandFailException {
		if (state == null) {
			displayMessage("no table" + SEPARATOR, out);
			return;
		}

		String key = args[0];
		String oldValue = state.remove(key);

		if (oldValue == null) {
			displayMessage("not found" + SEPARATOR, out);
		} else {
			displayMessage("removed" + SEPARATOR, out);
		}
	}
}