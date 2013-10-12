package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.File;
import java.io.OutputStream;

public class RmCommand extends AbstractCommand {
	public RmCommand() {
		super("rm", 1);
	};	

	public void execute(String[] args, Shell.ShellState state, OutputStream out) throws CommandFailException {		
		String pathToDelete = args[0];
		File toDelete = FileUtils.getAbsFile(pathToDelete, state);	

		if (toDelete.exists()) {
			FileUtils.recursiveDelete(toDelete);
		} else {
			throw new CommandFailException("rm: " + pathToDelete + " doesn't exist");
		}
	}
}