package ru.fizteh.fivt.students.elenav.commands;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.shell.State;

public class MakeDirectoryCommand extends AbstractCommand {
	public MakeDirectoryCommand(State s) { 
		super(s, "mkdir", 1);
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
