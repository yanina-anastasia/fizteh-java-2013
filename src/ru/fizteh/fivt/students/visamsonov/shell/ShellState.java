package ru.fizteh.fivt.students.visamsonov.shell;

import ru.fizteh.fivt.students.visamsonov.Database;
import java.io.*;
import java.util.TreeMap;

public class ShellState {
	
	private String currentDirectory = null;
	public Database database = new Database();

	public String getCurrentDirectory () {
		if (currentDirectory != null) {
			return currentDirectory;
		}
		try {
			currentDirectory = new File(".").getCanonicalPath();
			return currentDirectory;
		}
		catch (IOException e) {}
		return null;
	}

	public boolean setCurrentDirectory (String directory) {
		File file;
		try {
			if (new File(directory).isAbsolute()) {
				file = new File(directory);
			}
			else {
				file = new File(currentDirectory, directory);
			}
			if (!file.isDirectory()) {
				return false;
			}
			currentDirectory = file.getCanonicalPath();
			return true;
		}
		catch (IOException e) {}
		return false;
	}
}