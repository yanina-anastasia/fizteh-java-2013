package ru.fizteh.fivt.students.visamsonov.shell;

import java.io.*;
import java.nio.channels.FileChannel;

public class CommandCp extends CommandAbstract {

	public CommandCp () {
		this.name = "cp";
	}

	public void copyFile (File sourceFile, File destFile) throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}
		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		}
		finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}

	public boolean copy (String from, String to) {
		File source = new File(from);
		File dest = new File(to);
		if (!source.exists()) {
			return false;
		}
		if (source.isDirectory()) {
			if (!dest.isDirectory() && !dest.mkdir()) {
				return false;
			}
			try {
				String[] content = source.list();
				for (int i = 0; i < content.length; i++) {
					if (!copy(new File(from, content[i]).getCanonicalPath(), new File(to, content[i]).getCanonicalPath())) {
						return false;
					}
				}
			}
			catch (IOException e) {
				return false;
			}
		}
		else {
			try {
				copyFile(source, dest);
			}
			catch (IOException e) {
				return false;
			}
		}
		return true;
	}

	public void evaluate (String args) {
		String[] argArray = args.split("[\n\t ]+");
		if (argArray.length != 2) {
			printError("given " + argArray.length + " arguments, expected 2");
			return;
		}
		try {
			File sourceDirectory = new File(Utils.getCurrentDirectory(), argArray[0]);
			File destDirectory = new File(Utils.getCurrentDirectory(), argArray[1]);
			String destination = destDirectory.getCanonicalPath();
			String source = sourceDirectory.getCanonicalPath();
			if (destDirectory.isDirectory()) {
				destination = new File(destination, sourceDirectory.getName()).getCanonicalPath();
			}
			if (copy(source, destination)) {
				return;
			}
		}
		catch (IOException e) {}
		printError("can't copy \"" + argArray[0] + "\" to \"" + argArray[1] + "\"");
	}
}