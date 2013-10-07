package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.File;

public class MV extends Command {
	MV() {
		super("mv", 2);
	};

	//At this point, destination is guaranteed to be a directory,
	//which is preserved throughout the recursive traverse

	private void move(File source, File destination) throws CommandFailException {
		if (source.isFile()) {
			CP.copyFile(source, destination);
		} else {
			File newDestination = new File(destination, source.getName());
			if(!newDestination.exists()) {
				if (!newDestination.mkdir()) {
					throw new CommandFailException("mv: Unable to create directory: " + source.getName());
				}
			}

			for (String toMove : source.list()) {
				move(new File(source, toMove), newDestination);
			}
		}

		source.delete();
	}

	public void execute(String[] args, Shell.ShellState state) throws CommandFailException {	
		String sourcePath = args[0], destinationPath = args[1];

		File source = getAbsFile(sourcePath, state), destination = getAbsFile(destinationPath, state);

		if (!source.exists()) {
			throw new CommandFailException("mv: " + sourcePath + " doesn't exist");
		}

		//Renaming
		if ((source.getParent().equals(destination.getParent()))
		&& (!destination.isDirectory())) {

			if (!source.renameTo(destination)) {
				throw new CommandFailException("mv: Unable to rename " + sourcePath + " to " + destinationPath);
			}

			return;
		}

		if (!destination.isDirectory()) {
			throw new CommandFailException("mv: " + destination + " is not a directory");
		}

		move(source, destination);
	}
}