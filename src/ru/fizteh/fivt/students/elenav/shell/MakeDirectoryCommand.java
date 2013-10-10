package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class MakeDirectoryCommand extends AbstractCommand {
	MakeDirectoryCommand(ShellState s) { 
		setName("mkdir"); 
		setArgNumber(1);
		setShell(s);
	}
	public void execute(String args[], PrintStream s) throws IOException {
		File f = new File(absolutePath(args[1]));
		if (!f.exists()) {
			f.mkdir();
		} else {
			throw new IOException("mkdir: directory already exist");
		}
	}
}
