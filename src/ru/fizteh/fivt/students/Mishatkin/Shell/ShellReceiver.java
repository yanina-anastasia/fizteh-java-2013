package ru.fizteh.fivt.students.Mishatkin.Shell;

import javafx.scene.effect.ReflectionBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

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
		if (!Shell.isArgumentsMode) {
			System.out.print(s);
		}
	}

	private void println(String s) {
		if (!Shell.isArgumentsMode) {
			System.out.println(s);
		}
	}

	public synchronized static ShellReceiver sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new ShellReceiver();
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
	public void changeDirectoryCommand(String arg) throws FileNotFoundException {
		String previousStatePath = shellPath.getAbsolutePath();
		File destinationFile = new File(arg);
		FileNotFoundException notFoundException = new FileNotFoundException("cd: \'" + arg + "\': No such file or directory");
		if (destinationFile.isAbsolute()) {
			if (!destinationFile.exists()) {
				throw notFoundException;
			}
			shellPath = destinationFile;
			return;
		}
		String separatorRegularExpression = (File.separator.equals("/")) ? File.separator : "\\\\";
		String[] sequence = arg.split(separatorRegularExpression);
		synchronized (shellPath) {
			for (String simpleArg : sequence) {
				try {
					simpleChangeDirectory(simpleArg);
				} catch (FileNotFoundException e) {
					//  reverse transaction sequence
					shellPath = new File(previousStatePath);
					throw notFoundException;
				}
			}
		}
	}

	private void simpleChangeDirectory(String arg) throws FileNotFoundException {
		if (arg.equals("..")) {
			if (shellPath.getParent() != null) {
				shellPath = shellPath.getParentFile();
			} else {
				throw new FileNotFoundException("No such directory.");
			}
		} else {
			File destination = new File(shellPath, arg);
			if (!(destination.exists() && destination.isDirectory()) || arg.length() == 0) {
				throw new FileNotFoundException("No such directory.");
			} else {
				shellPath = destination;
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
	public void removeCommand(String arg) throws IOException {
		File absolutePathFile = new File(arg);
		if (absolutePathFile.isAbsolute()) {
			deleteFile(absolutePathFile);
		} else {
			File fileToDelete = new File(shellPath, arg);
			deleteFile(fileToDelete);
		}
	}

	private void deleteFile(File fileToDelete) throws IOException {
		if (!fileToDelete.exists()) {
			throw new IOException("rm: cannot remove \'" + fileToDelete.getName() + "\': No such file or directory");
		}
		fileToDelete.delete();
	}

	@Override
	public void copyCommand(String sourceFileOrDirectoryName, String destinationDirectoryName) throws Exception {
		File sourceFileOrDirectory = new File(sourceFileOrDirectoryName);
		if (!sourceFileOrDirectory.isAbsolute()) {
			sourceFileOrDirectory = new File(shellPath, sourceFileOrDirectoryName);
		}
		if (!sourceFileOrDirectory.exists()) {
			throw new Exception("cp: \'" + sourceFileOrDirectory + "\' : No such file or directory");
		}
		File destinationDirectory = new File(destinationDirectoryName);
		if (!destinationDirectory.isAbsolute()) {
			destinationDirectory = new File(shellPath, destinationDirectoryName);
		}
		File destinationFileOrDirectory = new File(destinationDirectory, sourceFileOrDirectory.getName());
		while (destinationFileOrDirectory.exists()) {
			destinationFileOrDirectory = new File(destinationFileOrDirectory.getAbsoluteFile() + " (copy)");
		}
		try {
			Files.copy(sourceFileOrDirectory.toPath(), destinationFileOrDirectory.toPath());
		} catch (IOException e) {
			throw new Exception("cp: \'" + sourceFileOrDirectoryName + "\' -> \'" + destinationDirectoryName +
			                    "\' : Cannot copy file or directory");
		}
	}

	@Override
	public void moveCommand(String sourceFileOrDirectoryName, String destinationFileOrDirectoryName) throws Exception {
		File sourceFileOrDirectory = new File(sourceFileOrDirectoryName);
		if (!sourceFileOrDirectory.isAbsolute()) {
			sourceFileOrDirectory = new File(shellPath, sourceFileOrDirectoryName);
		}
		if (!sourceFileOrDirectory.exists()) {
			throw new Exception("mv: \'" + sourceFileOrDirectory + "\' : No such file or directory");
		}
		File destinationFileOrDirectory = new File(destinationFileOrDirectoryName);
		if (!destinationFileOrDirectory.isAbsolute()) {
			destinationFileOrDirectory = new File(shellPath, destinationFileOrDirectoryName);
		}
		if (destinationFileOrDirectory.exists()) {
			removeCommand(destinationFileOrDirectory.getAbsolutePath());
		}
		try {
			Files.move(sourceFileOrDirectory.toPath(), destinationFileOrDirectory.toPath());
		} catch (IOException e) {
			throw new Exception("mv: \'" + sourceFileOrDirectoryName + "\' -> \'" + destinationFileOrDirectoryName +
			                    "\' : Cannot move file or directory");
		}
	}
}
