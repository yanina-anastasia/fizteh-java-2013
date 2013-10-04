package ru.fizteh.fivt.students.mishatkin.shell;

import java.io.*;
import java.nio.file.Files;

/**
 * ShellReceiver.java
 * Created by Vladimir Mishatkin on 9/24/13
 *
 */

public class ShellReceiver implements CommandReceiver {
	private static ShellReceiver sharedInstance = null;
	private File shellPath;

	private ShellReceiver() {
		shellPath = File.listRoots()[0];
	}

	private void print(String s) {
		System.out.print(s);
	}

	private void println(String s) {
		System.out.println(s);
	}

	public static ShellReceiver sharedInstance() {
		if (sharedInstance == null) {
			synchronized (ShellReceiver.class)
			{
				if (sharedInstance == null)
				{
					sharedInstance = new ShellReceiver();
				}
			}
		}
		return sharedInstance;
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

	@Override
	public void changeDirectoryCommand(String arg) throws ShellException {
		File previousState = new File(shellPath, "");
		File destinationFile = new File(arg);
		if (!destinationFile.isAbsolute()) {
			destinationFile = new File(shellPath, arg);
		}
		if (!destinationFile.exists() || !destinationFile.exists()) {
			throw new ShellException("cd: \'" + arg + "\' : No such directory");
		}
		String separatorRegularExpression = (File.separator.equals("/")) ? File.separator : "\\\\";
		String[] sequence = arg.split(separatorRegularExpression);
		for (String simpleArg : sequence) {
			File nextDirectory = new File(shellPath, simpleArg);
			if (!nextDirectory.exists() || !nextDirectory.isDirectory()) {
				shellPath = previousState;
				throw new ShellException("cd: \'" + arg + "\' : No such directory");
			} else {
				if (simpleArg.equals("..")) {
					if (shellPath.getParent() != null) {
						shellPath = shellPath.getParentFile();
					} else {
						throw new ShellException("cd: \'" + arg +"\' :No such directory.");
					}
				} else {
					shellPath = new File(nextDirectory, "");
				}
			}
		}
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
	public void makeDirectoryCommand(String arg) {
		File absolutePathFile = new File(arg);
		if (absolutePathFile.isAbsolute() && !absolutePathFile.exists()) {
			absolutePathFile.mkdir();
		} else {
			File directoryToCreate = new File(shellPath, arg);
			if (!directoryToCreate.exists()) {
				directoryToCreate.mkdir();
			}
		}
	}

	@Override
	public void removeCommand(String arg) throws ShellException {
		File absolutePathFile = new File(arg);
		if (absolutePathFile.isAbsolute()) {
			deleteFile(absolutePathFile);
		} else {
			File fileToDelete = new File(shellPath, arg);
			deleteFile(fileToDelete);
		}
	}

	private void deleteFile(File fileToDelete) throws ShellException {
		if (!fileToDelete.exists()) {
			throw new ShellException("rm: cannot remove \'" + fileToDelete.getName() + "\': No such file or directory");
		}
		fileToDelete.delete();
	}

	@Override
	public void copyCommand(String sourceFileOrDirectoryName, String destinationDirectoryName) throws ShellException {
		File sourceFileOrDirectory = new File(sourceFileOrDirectoryName);
		if (!sourceFileOrDirectory.isAbsolute()) {
			sourceFileOrDirectory = new File(shellPath, sourceFileOrDirectoryName);
		}
		if (!sourceFileOrDirectory.exists()) {
			throw new ShellException("cp: \'" + sourceFileOrDirectory + "\' : No such file or directory");
		}
		File destinationDirectory = new File(destinationDirectoryName);
		if (!destinationDirectory.isAbsolute()) {
			destinationDirectory = new File(shellPath, destinationDirectoryName);
		}
		File destinationFileOrDirectory = new File(destinationDirectory, sourceFileOrDirectory.getName());
		if (destinationFileOrDirectory.exists()) {
			throw new ShellException("cp: \'" + destinationFileOrDirectory.getAbsolutePath() + "\' : Destination directory already exists");
		}
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
			                    "\' : Cannot copy file or directory");
		}
	}

	@Override
	public void moveCommand(String sourceFileOrDirectoryName, String destinationFileOrDirectoryName) throws ShellException {
		File sourceFileOrDirectory = new File(sourceFileOrDirectoryName);
		if (!sourceFileOrDirectory.isAbsolute()) {
			sourceFileOrDirectory = new File(shellPath, sourceFileOrDirectoryName);
		}
		if (!sourceFileOrDirectory.exists()) {
			throw new ShellException("mv: \'" + sourceFileOrDirectory + "\' : No such file or directory");
		}
		File destinationFileOrDirectory = new File(destinationFileOrDirectoryName);
		if (!destinationFileOrDirectory.isAbsolute()) {
			destinationFileOrDirectory = new File(shellPath, destinationFileOrDirectoryName);
		}
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
