package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.shell.AbstractCommand;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;

public class CreateCommand extends AbstractMultiTableDataBaseCommand {
	public CreateCommand() {
		super("create", 1);
	}

	public void execute(String[] args, MultiTableDataBase state, OutputStream out) throws CommandFailException {
		String tablename = args[0];

		if (!state.create(tablename)) {
			displayMessage(tablename + " exists" + SEPARATOR, out);
			return;
		}

		if (!(new File(state.getRoot(), tablename).mkdir())) {
			throw new CommandFailException("create: Unable to create directory");
		}

		displayMessage("created" + SEPARATOR, out);
	}
}