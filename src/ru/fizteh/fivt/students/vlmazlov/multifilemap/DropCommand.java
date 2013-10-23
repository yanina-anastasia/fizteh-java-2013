package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.shell.FileUtils;
import ru.fizteh.fivt.students.vlmazlov.shell.AbstractCommand;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import java.io.OutputStream;
import ru.fizteh.fivt.students.vlmazlov.shell.AbstractCommand;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import java.io.IOException;
import java.io.File;

public class DropCommand extends AbstractMultiTableDataBaseCommand {
	public DropCommand() {
		super("drop", 1);
	}

	public void execute(String[] args, MultiTableDataBase state, OutputStream out) throws CommandFailException {
		String tablename = args[0];
		File directory = state.drop(tablename);

		if (directory == null) {
			displayMessage(tablename + " not exists" + SEPARATOR, out);
		}

		FileUtils.recursiveDelete(directory);

		displayMessage("dropped" + SEPARATOR, out);
	}
}