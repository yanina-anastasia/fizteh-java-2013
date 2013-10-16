package ru.fizteh.fivt.students.vlmazlov.filemap;

import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import ru.fizteh.fivt.students.vlmazlov.shell.Shell;
import java.io.OutputStream;
import java.io.IOException;

public class RemoveCommand extends FileMapCommand {
	public RemoveCommand(FileMap fileMap) {
		super("remove", 1, fileMap);
	};

	public void execute(String[] args, Shell.ShellState state, OutputStream out) throws CommandFailException {
		String key = args[0];
		String output;
		String oldValue = fileMap.removeFromFileMap(key);

		if (oldValue == null) {
			output = "not found" + System.getProperty("line.separator");
		} else {
			output = "removed" + System.getProperty("line.separator");
		}

		try {
			out.write(output.getBytes());
		} catch (IOException ex) {
			throw new CommandFailException("put: Unable to display result message");
		}
	}
}