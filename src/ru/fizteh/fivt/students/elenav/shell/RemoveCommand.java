package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class RemoveCommand extends AbstractCommand {
	RemoveCommand(ShellState s) { 
		super(s, "rm", 1);
	}
	
	private void deleteRecursively(String path) throws IOException {
		File f = new File(path);
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File file : files) {
				deleteRecursively(file.getAbsolutePath());
			}
		}
		if (!f.delete()) {
			throw new IOException("rm: cannot remove '" + f.getName() + "': Unknown error");
		}
	}
	
	public void execute(String args[], PrintStream s) throws IOException {
		File f = new File(absolutePath(args[1]));
		if (!f.exists()) {
			throw new IOException("rm: cannot remove '" + args[1] + "': No such file or directory");
		} else {
			deleteRecursively(f.getAbsolutePath());
		}
	}
}

