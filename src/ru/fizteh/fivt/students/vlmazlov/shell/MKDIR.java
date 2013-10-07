package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.File;

public class MKDIR extends Command {
	MKDIR() {
		super("mkdir", 1);
	};

	public void execute(String[] args, Shell.ShellState state) throws CommandFailException {		
		String dirname = args[0];

		File toBeCreated = getAbsFile(dirname, state);

		if (!toBeCreated.mkdir()) {
			throw new CommandFailException("mkdir: Unable to create directory: " + dirname);
		}
	}
}