package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.File;

public class RM extends Command {
	RM() {
		super("rm", 1);
	};	

	private void delete(File toDelete) {
		if ((toDelete.isFile()) || (0 == toDelete.list().length)) {
			toDelete.delete();
			return;
		}

		String[] listing = toDelete.list();

		for (String entry : listing) {
			delete(new File (toDelete, entry));
		}

		toDelete.delete();
	}

	public void execute(String[] args, Shell.ShellState state) throws CommandFailException {		
		String pathToDelete = args[0];
		File toDelete = super.getAbsFile(pathToDelete, state);	

		if (toDelete.exists()) {
			delete(toDelete);
		} else {
			throw new CommandFailException("rm: " + pathToDelete + " doesn't exist");
		}
	}
}