package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.File;
import java.io.OutputStream;

public class MkdirCommand extends AbstractCommand {
	public MkdirCommand() {
		super("mkdir", 1);
	};

	public void execute(String[] args, Shell.ShellState state, OutputStream out) throws CommandFailException {		
		String dirname = args[0];

		File toBeCreated = FileUtils.getAbsFile(dirname, state);

		if (!toBeCreated.mkdir()) {
			throw new CommandFailException("mkdir: Unable to create directory: " + dirname);
		}
	}
}