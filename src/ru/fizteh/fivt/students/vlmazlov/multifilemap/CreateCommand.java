package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import java.io.OutputStream;

public class CreateCommand extends AbstractDataBaseCommand {
	public CreateCommand() {
		super("create", 1);
	}

	public void execute(String[] args, DataBaseState state, OutputStream out) throws CommandFailException {
		String tablename = args[0];

		if (state.getProvider().getTable(tablename) != null) {
			displayMessage(tablename + " exists" + SEPARATOR, out);
			return;
		}

		state.getProvider().createTable(tablename);

		displayMessage("created" + SEPARATOR, out);
	}
}