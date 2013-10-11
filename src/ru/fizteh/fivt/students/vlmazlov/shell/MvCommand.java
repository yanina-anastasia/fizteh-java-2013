package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.File;
import java.io.OutputStream;
import java.io.IOException;

public class MvCommand extends AbstractCommand {
	public MvCommand() {
		super("mv", 2);
	};

	//At this point, destination is guaranteed to be a directory,
	//which is preserved throughout the recursive traverse

	public void execute(String[] args, Shell.ShellState state, OutputStream out) throws CommandFailException {	
		String sourcePath = args[0], destinationPath = args[1];

		File source = FileUtils.getAbsFile(sourcePath, state), destination = FileUtils.getAbsFile(destinationPath, state);

		if (!source.exists()) {
			throw new CommandFailException("mv: " + sourcePath + " doesn't exist");
		}

		//Renaming
		try {
			if ((source.getParentFile().getCanonicalPath().equals(destination.getParentFile().getCanonicalPath()))
			&& (!destination.isDirectory())) {

				if (!source.renameTo(destination)) {
					throw new CommandFailException("mv: Unable to rename " + sourcePath + " to " + destinationPath);
				}

				return;
			}
		} catch (IOException ex) {
			throw new CommandFailException("mv: Unable to discern parent directories");
		}

		if (!destination.isDirectory()) {
			throw new CommandFailException("mv: " + destination + " is not a directory");
		}

		try {
			FileUtils.moveToDir(source, destination);
		} catch (FileOperationFailException ex) {
			throw new CommandFailException("mv: " + ex.getMessage());
		}
	}
}