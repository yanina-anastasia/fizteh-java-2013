package ru.fizteh.fivt.students.elenav.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.shell.FilesystemState;

public class CopyCommand extends AbstractCommand {
	public CopyCommand(FilesystemState s) { 
		super(s, "cp", 2);
	}
	public void execute(String args[], PrintStream s) throws IOException {
		File sourse = new File(absolutePath(args[1]));
		File destination = new File(absolutePath(args[2]));
		if (!sourse.getCanonicalPath().equals(destination.getCanonicalPath())) {
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
				try {
					while ((count = inputStream.read(buffer)) > 0) {
						outputStream.write(buffer, 0, count);
					}
				} catch (IOException e) {
					throw new IOException("Can't read or write");
				}
			}
		} else {
			throw new IOException("cp: cannot copy: Files are same");
		}
	}
}