package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Closeable;

public class CP extends Command {
	CP() {
		super("cp", 2);
	};	

	private static void closeQuietly(Closeable resource) {
		try {
			if (null != resource) {
				resource.close();
			}
		} catch (IOException ex) {
			System.err.println( "cp: Exception during Stream.close()" + ex.getMessage());	
		}	
	}

	public static void copyFile(File sourceFile, File destinationDir) throws CommandFailException {
		File copiedFile = new File(destinationDir, sourceFile.getName()); 

		FileInputStream original = null;
		FileOutputStream copy = null;

		try {
			copiedFile.createNewFile();

			original = new FileInputStream(sourceFile);
			copy = new FileOutputStream(copiedFile);
			
			byte[] buffer = new byte[1024];
			int bytesRead;

			while (0 < (bytesRead = original.read(buffer))) {
				copy.write(buffer, 0, bytesRead);
			}

		} catch(FileNotFoundException ex) {
			throw new CommandFailException("cp: " + ex.getMessage());
		} catch(IOException ex) {
			throw new CommandFailException("cp: " + ex.getMessage());
		} finally {
			try {
				original.close();
				copy.close();
			} catch (IOException ex) {
				closeQuietly(original);
				closeQuietly(copy);
			}
		}
	}

	private void copy(File source, File destination) throws CommandFailException {
		if (source.isFile()) {
			copyFile(source, destination);
			return;
		}

		File newDestination = new File(destination, source.getName());
		if(!newDestination.exists()) {
			if (!newDestination.mkdir()) {
				throw new CommandFailException("mv: Unable to create directory: " + source.getName());
			}
		}

		for (String toCopy : source.list()) {
			copy(new File(source, toCopy), newDestination);
		}
	}

	public void execute(String[] args, Shell.ShellState state) throws CommandFailException {	
		String source = args[0], destination = args[1];

		File sourceFile = getAbsFile(source, state), destinationDir = getAbsFile(destination, state);

		if (!destinationDir.isDirectory()) {
			throw new CommandFailException("cp: " + destination + " is not a directory");
		}

		copy(sourceFile, destinationDir);
	}
}