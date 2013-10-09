package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;

class RemoveCommand extends Command {
	RemoveCommand(ShellState s) { 
		name = "rm"; 
		argNumber = 1;
		shell = s;
	}
	
	void deleteRecursively(String path) throws IOException {
		File f = new File(path);
		File[] files = f.listFiles();
		for (File file : files) {
			deleteRecursively(file.getAbsolutePath());
		}
		if (!f.delete()) {
			throw new IOException("rm: cannot remove '" + f.getName() + "': Unknown error");
		}
	}
	
	void execute(String args[]) throws IOException {
		File f = new File(absolutePath(args[1]));
		if (!f.exists()) {
			throw new IOException("rm: cannot remove '" + args[1] + "': No such file or directory");
		} else {
			deleteRecursively(f.getAbsolutePath());
		}
	}
}

