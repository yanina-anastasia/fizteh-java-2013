package ru.fizteh.fivt.students.vlmazlov.filemap;

import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import java.io.OutputStream;
import java.io.IOException;

public class RemoveCommand extends AbstractFileMapCommand {
	public RemoveCommand() {
		super("remove", 1);
	}

	public void execute(String[] args, FileMapState state, OutputStream out) throws CommandFailException {
		FileMap fileMap = state.getFileMap();
		
		if (fileMap == null) {
			displayMessage("no table" + SEPARATOR, out);
			return;
		}

		String key = args[0];
		String oldValue = fileMap.remove(key);

		if (oldValue == null) {
			displayMessage("not found" + SEPARATOR, out);
		} else {
			displayMessage("removed" + SEPARATOR, out);
		}
	}
}