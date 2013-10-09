package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;

class MakeDirectoryCommand extends Command {
	MakeDirectoryCommand(ShellState s) { 
		name = "mkdir"; 
		argNumber = 1;
		shell = s;
	}
	void execute(String args[]) throws IOException {
		File f = new File(absolutePath(args[1]));
		if (!f.exists()) {
			f.mkdir();
		} else {
			throw new IOException("mkdir: directory already exist");
		}
	}
}
