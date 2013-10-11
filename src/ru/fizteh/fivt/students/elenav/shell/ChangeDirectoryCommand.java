package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class ChangeDirectoryCommand extends AbstractCommand {
	public ChangeDirectoryCommand(ShellState s) { 
		super(s, "cd", 1); 
	}
	public void execute(String args[], PrintStream s) throws IOException {
		File f = new File(absolutePath(args[1]));
		if (f.isDirectory())
		{
			setWorkingDirectory(f);
		} else {
			throw new IOException("cd: '" + args[1] +"': No such file or directory");
		}
	}
}
