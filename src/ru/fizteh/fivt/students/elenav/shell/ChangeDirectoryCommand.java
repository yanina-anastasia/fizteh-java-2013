package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;

public class ChangeDirectoryCommand extends AbstractCommand {
	public ChangeDirectoryCommand(ShellState s) { 
		setName("cd"); 
		setArgNumber(1);
		setShell(s);
	}
	public void execute(String args[]) throws IOException {
		File f = new File(args[1]);
		if (f.isDirectory())
		{
			setWorkingDirectory(f);
		} else {
			throw new IOException("cd: '" + args[1] +"': No such file or directory");
		}
	}
}
