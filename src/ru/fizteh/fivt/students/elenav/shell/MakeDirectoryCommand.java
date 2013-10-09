package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;

public class MakeDirectoryCommand extends AbstractCommand {
	MakeDirectoryCommand(ShellState s) { 
		setName("mkdir"); 
		setArgNumber(1);
		setShell(s);
	}
	public void execute(String args[]) throws IOException {
		File f = new File(absolutePath(args[1]));
		if (!f.exists()) {
			f.mkdir();
		} else {
			throw new IOException("mkdir: directory already exist");
		}
	}
}
