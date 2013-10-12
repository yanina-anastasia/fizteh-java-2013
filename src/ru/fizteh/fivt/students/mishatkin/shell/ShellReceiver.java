package ru.fizteh.fivt.students.mishatkin.shell;

import java.io.*;
import java.nio.file.Files;

/**
 * ShellReceiver.java
 * Created by Vladimir Mishatkin on 9/24/13
 *
 */

public class ShellReceiver implements CommandReceiver {

	private File shellPath;

	public ShellReceiver() {
		shellPath = new File(".");
		try {
			shellPath = shellPath.getCanonicalFile();
		} catch (IOException e) {

		}
	}

	private void print(String s) {
		System.out.print(s);
	}

	private void println(String s) {
		System.out.println(s);
	}

	private String simplePrompt() {
		return "$";
	}

	public void showPrompt() {
		if (!Shell.isArgumentsMode) {
			print(simplePrompt() + " ");
//			System.out.print(shellPath.getAbsolutePath() + " " + simplePrompt() + " ");
		}
	}

	private File normalizedFile(String arg) {
		File theFile = new File(arg);
		if (!theFile.isAbsolute()) {
			theFile = new File(shellPath, arg);
		}
		try {
			theFile = theFile.getCanonicalFile();
		} catch (IOException ignored) {
		}
		return theFile;
	}

	@Override
	public void changeDirectoryCommand(String arg) throws ShellException {
		//File previousState = new File(shellPath, "");
		File destinationFile = normalizedFile(arg);
		if (!destinationFile.exists() || !destinationFile.exists()) {
			throw new ShellException("cd: \'" + arg + "\' : No such directory");
		}
		shellPath = destinationFile;
	}

	@Override
	public void exitCommand() throws TimeToExitException {
		throw new TimeToExitException();
	}

	@Override
	public void directoryCommand() {
		File[] files = shellPath.listFiles();
		for (File file : files) {
			println(file.getName());
		}
	}

	@Override
	public void printWorkingDirectoryCommand() {
		println(shellPath.getAbsolutePath());
	}

	@Override
	public void makeDirectoryCommand(String arg) throws ShellException {
		File directoryToCreate = normalizedFile(arg);
		if (directoryToCreate.exists()) {
			throw new ShellException("mkdir: \'" + directoryToCreate.getAbsolutePath() + "\': directory already exists.");
		}
		directoryToCreate.mkdir();
	}

	@Override
	public void removeCommand(String arg) throws ShellException {
		File fileToDelete = normalizedFile(arg);
		deleteFile(fileToDelete);
	}

	private void deleteFile(File fileToDelete) throws ShellException {
		if (!fileToDelete.exists() || !fileToDelete.delete()) {
			throw new ShellException("rm: cannot remove \'" + fileToDelete.getName() + "\': No such file or directory");
		}
	}

	@Override
	public void copyCommand(String sourceFileOrDirectoryName, String destinationDirectoryName) throws ShellException {
		File sourceFileOrDirectory = normalizedFile(sourceFileOrDirectoryName);
		if (!sourceFileOrDirectory.exists()) {
			throw new ShellException("cp: \'" + sourceFileOrDirectory + "\' : No such file or directory");
		}
		File destinationDirectory = normalizedFile(destinationDirectoryName);
		File destinationFileOrDirectory = new File(destinationDirectory, sourceFileOrDirectory.getName());
		if (destinationFileOrDirectory.exists()) {
			throw new ShellException("cp: \'" + destinationFileOrDirectory.getAbsolutePath() + "\' : Destination directory already exists");
		}
		if (!destinationFileOrDirectory.isDirectory()) {
			if (destinationDirectory.toPath().equals(sourceFileOrDirectory.toPath())) {
				throw new ShellException("cp: \'" + sourceFileOrDirectoryName + "\' : cannot copy to the same path.");
			} else {
				try {
					Files.copy(sourceFileOrDirectory.toPath(), destinationDirectory.toPath());
				} catch (IOException e) {
					throw new ShellException("cp: \'" + sourceFileOrDirectoryName + "\' -> \'" + destinationDirectoryName +
							"\' : Cannot copy file.");
				}
			}
		} else {
			try {
				Files.copy(sourceFileOrDirectory.toPath(), destinationFileOrDirectory.toPath());

				File[] subFilesAndDirectories = sourceFileOrDirectory.listFiles();
				if (subFilesAndDirectories != null) {
					for (File subFile : subFilesAndDirectories) {
						String destinationSubFileOwnerName = new File(destinationDirectory.getAbsolutePath(),
																	  sourceFileOrDirectory.getName()).getAbsolutePath();
						copyCommand(subFile.getAbsolutePath(), destinationSubFileOwnerName);
					}
				}
			} catch (IOException e) {
				throw new ShellException("cp: \'" + sourceFileOrDirectoryName + "\' -> \'" + destinationDirectoryName +
									"\' : Cannot copy file or directory.");
			}
		}
	}

	@Override
	public void moveCommand(String sourceFileOrDirectoryName, String destinationFileOrDirectoryName) throws ShellException {
		File sourceFileOrDirectory = normalizedFile(sourceFileOrDirectoryName);
		if (!sourceFileOrDirectory.exists()) {
			throw new ShellException("mv: \'" + sourceFileOrDirectory + "\' : No such file or directory");
		}
		File destinationFileOrDirectory = normalizedFile(destinationFileOrDirectoryName);
		if (destinationFileOrDirectory.exists()) {
			throw new ShellException("mv : \'" + destinationFileOrDirectory.getAbsolutePath() + "\' : destination file already exists.");
		}
		try {
			Files.move(sourceFileOrDirectory.toPath(), destinationFileOrDirectory.toPath());
		} catch (IOException e) {
			throw new ShellException("mv: \'" + sourceFileOrDirectoryName + "\' -> \'" + destinationFileOrDirectoryName +
			                    "\' : Cannot move file or directory");
		}
	}
}
