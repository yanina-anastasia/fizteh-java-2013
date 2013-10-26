package ru.fizteh.fivt.students.vlmazlov.filemap;

import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import java.io.OutputStream;

public class GetCommand extends AbstractFileMapCommand {
	public GetCommand() {
		super("get", 1);
	}

	public void execute(String[] args, FileMapState state, OutputStream out) throws CommandFailException {
		FileMap fileMap = state.getFileMap();

		if (fileMap == null) {
			displayMessage("no table" + SEPARATOR, out);
			return;
		}

		String key = args[0];
		String value = fileMap.get(key);

		if (value == null) {
			displayMessage("not found" + SEPARATOR, out);
		} else {
			displayMessage("found" + SEPARATOR + value + SEPARATOR, out);
		}
	}
}