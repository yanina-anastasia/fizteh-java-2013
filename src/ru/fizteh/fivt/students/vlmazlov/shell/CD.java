package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.File;
import java.io.IOException;

public class CD extends Command {
	CD() {
		super("cd", 1);
	};	

	public void execute(String[] args, Shell.ShellState state) throws CommandFailException {	
		String newPath = args[0];

		File newDir = getAbsFile(newPath, state);
		
		if (!newDir.isDirectory()) {
			throw new CommandFailException("cd: " + newPath + " is not a directory");
		}
			
		try {
			state.changeCurDir(newDir.getCanonicalPath());
		} catch (IOException ex) {
			throw new CommandFailException("cd: " + ex.getMessage());
		}
	}
}
