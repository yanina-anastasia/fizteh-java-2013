package ru.fizteh.fivt.students.visamsonov.shell;

import java.io.*;

public class Utils {
	
	private static String currentDirectory = null;

	public static String stringArrayJoin (String[] array, String separator) {
		if (array.length == 0) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		result.append(array[0]);
		for (int i = 1; i < array.length; ++i) {
			result.append(separator).append(array[i]);
		}
		return result.toString();
	}

	public static String getCurrentDirectory () {
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

	public static boolean setCurrentDirectory (String directory) {
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