package ru.fizteh.fivt.students.vlmazlov.filemap;

import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import java.io.OutputStream;
import java.io.IOException;

public class PutCommand extends AbstractFileMapCommand {
	public PutCommand() {
		super("put", 2);
	}

	public void execute(String[] args, FileMapState state, OutputStream out) throws CommandFailException {
		FileMap fileMap = state.getFileMap();

		if (fileMap == null) {
			displayMessage("no table" + SEPARATOR, out);
			return;
		}

		String key = args[0], value = args[1];
		String oldValue = fileMap.put(key, value);

		if (oldValue == null) {
			displayMessage("new" + SEPARATOR, out);
		} else {
			displayMessage("overwrite" + SEPARATOR + oldValue + SEPARATOR, out);
		}
	}
}