package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;

public class ChangeDirectoryCommand extends Command {
	public ChangeDirectoryCommand(ShellState s) { 
		name = "cd"; 
		argNumber = 1;
		shell = s;
	}
	void execute(String args[]) throws IOException {
		File f = new File(args[1]);
		if (f.isDirectory())
		{
			shell.workingDirectory = f;
		} else {
			throw new IOException("cd: '" + args[1] +"': No such file or directory");
		}
	}
}
