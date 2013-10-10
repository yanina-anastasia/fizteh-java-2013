package ru.fizteh.fivt.students.visamsonov.shell;

import java.io.*;
import java.nio.channels.FileChannel;

public class CommandCp extends CommandAbstract {

	public CommandCp () {
		this.name = "cp";
	}

	public void copyFile (File sourceFile, File destFile) throws IOException {
		destFile.createNewFile();
		FileChannel source = null;
		FileChannel destination = null;
		source = new FileInputStream(sourceFile).getChannel();
		destination = new FileOutputStream(destFile).getChannel();
		if (source != null && destination != null) {
			destination.transferFrom(source, 0, source.size());
		}
		if (source != null) {
			source.close();
		}
		if (destination != null) {
			destination.close();
		}
	}

	public boolean copy (String from, String to) throws IOException {
		File source = new File(from);
		File dest = new File(to);
		if (!source.exists() || dest.exists()) {
			return false;
		}
		if (source.isDirectory()) {
			if (!dest.mkdir()) {
				return false;
			}
			String[] content = source.list();
			for (int i = 0; i < content.length; i++) {
				if (!copy(new File(from, content[i]).getCanonicalPath(), new File(to, content[i]).getCanonicalPath())) {
					return false;
				}
			}
		}
		else {
			copyFile(source, dest);
		}
		return true;
	}

	public boolean evaluate (ShellState state, String args) {
		String[] argArray = splitArguments(args);
		if (!checkFixedArguments(argArray, 2)) {
			return false;
		}
		try {
			File sourceDirectory = new File(state.getCurrentDirectory(), argArray[0]);
			File destDirectory = new File(state.getCurrentDirectory(), argArray[1]);
			String destination = destDirectory.getCanonicalPath();
			String source = sourceDirectory.getCanonicalPath();
			if (destDirectory.isDirectory()) {
				destination = new File(destination, sourceDirectory.getName()).getCanonicalPath();
			}
			if (copy(source, destination)) {
				return true;
			}
		}
		catch (IOException e) {
			printError("can't copy \"" + argArray[0] + "\" to \"" + argArray[1] + "\": " + e.getMessage());
			return false;
		}
		printError("can't copy \"" + argArray[0] + "\" to \"" + argArray[1] + "\"");
		return false;
	}
}