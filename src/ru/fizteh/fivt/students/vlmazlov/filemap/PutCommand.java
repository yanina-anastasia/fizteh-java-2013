package ru.fizteh.fivt.students.vlmazlov.filemap;

import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import ru.fizteh.fivt.students.vlmazlov.shell.Shell;
import java.io.OutputStream;
import java.io.IOException;

public class PutCommand extends FileMapCommand {
	public PutCommand(FileMap fileMap) {
		super("put", 2, fileMap);
	};

	public void execute(String[] args, Shell.ShellState state, OutputStream out) throws CommandFailException {
		String key = args[0], value = args[1];
		String output;
		String oldValue = fileMap.addToFileMap(key, value);

		if (oldValue == null) {
			output = "new" + separator;
		} else {
			output = "overwrite" + separator + oldValue + separator;
		}

		try {
			out.write(output.getBytes());
		} catch (IOException ex) {
			throw new CommandFailException("put: Unable to display result message");
		}
	}
}