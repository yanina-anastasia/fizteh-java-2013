package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.File;
import java.io.OutputStream;
import java.io.IOException;

public class CpCommand extends AbstractCommand {
	public CpCommand() {
		super("cp", 2);
	};	

	public void execute(String[] args, Shell.ShellState state, OutputStream out) throws CommandFailException {	
		String sourcePath = args[0], destinationPath = args[1];

		File source = FileUtils.getAbsFile(sourcePath, state), destination = FileUtils.getAbsFile(destinationPath, state);

		if (!source.exists()) {
			throw new CommandFailException("cp: " + sourcePath + " doesn't exist");
		}

		if (!destination.isDirectory()) {
			if (destination.isFile()) {
				//destination exists
				throw new CommandFailException("cp: Unable to copy " + sourcePath + " to " + destinationPath);
			//otherwise, it doesn't exist
			} else if (destination.getParentFile().exists()) {
				try { 
					FileUtils.copyToFile(source, destination);
				} catch (FileOperationFailException ex) {
					throw new CommandFailException("cp: Unable to create file " + destinationPath + ": " + ex.getMessage());
				}
			} else {
				throw new CommandFailException("cp: Unable to copy " + sourcePath + " to " + destinationPath);
			}
		} else {
			try {
				FileUtils.recursiveCopy(source, destination);
			} catch (FileOperationFailException ex) {
				throw new CommandFailException("cp: " + ex.getMessage());
			}
		}
	}
}