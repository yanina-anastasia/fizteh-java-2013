package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

class CopyCommand extends Command {
	CopyCommand(ShellState s) { 
		name = "cp"; 
		argNumber = 2;
		shell = s;
	}
	void execute(String args[]) throws IOException {
		File sourse = new File(absolutePath(args[1]));
		File destination = new File(absolutePath(args[2]));
		if (args[1].equals(args[2])) {
			throw new IOException("Files are same");
		}
		if (!sourse.exists() || !sourse.canRead()) {
			throw new IOException("cp: cannot copy '" + args[1] + "' to '" + args[2] +
								"': No such file or directory");
		} else {
			if (!destination.isDirectory()) {
				destination.createNewFile();
			}
			FileInputStream inputStream = new FileInputStream(sourse);
			FileOutputStream outputStream = new FileOutputStream(destination);
			byte[] buffer = new byte[4096];
			int count = 0;
			while ((count = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, count);
			}			
		}
	}
}