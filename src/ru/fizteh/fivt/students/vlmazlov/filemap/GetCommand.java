package ru.fizteh.fivt.students.vlmazlov.filemap;

import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import ru.fizteh.fivt.students.vlmazlov.shell.Shell;
import java.io.OutputStream;
import java.io.IOException;

public class GetCommand extends FileMapCommand {
	public GetCommand(FileMap fileMap) {
		super("get", 1, fileMap);
	};

	public void execute(String[] args, Shell.ShellState state, OutputStream out) throws CommandFailException {
		String key = args[0];
		String output;
		String value = fileMap.findInFileMap(key);

		if (value == null) {
			output = "not found" + System.getProperty("line.separator");
		} else {
			output = "found" + System.getProperty("line.separator") + value + System.getProperty("line.separator");
		}

		try {
			out.write(output.getBytes());
		} catch (IOException ex) {
			throw new CommandFailException("put: Unable to display result message");
		}
	}
}