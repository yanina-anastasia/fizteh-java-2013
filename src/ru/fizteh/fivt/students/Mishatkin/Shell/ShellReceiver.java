package ru.fizteh.fivt.students.Mishatkin.Shell;

import javafx.scene.effect.ReflectionBuilder;

import java.io.*;
import java.nio.file.Files;
import java.util.regex.Pattern;

/**
 * ShellReceiver.java
 * Created by Vladimir Mishatkin on 9/24/13
 *
 */

public class ShellReceiver {
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

	public void changeDirectory(String arg) throws FileNotFoundException {
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

	public void exitCommand() throws TimeToExitException {
		throw new TimeToExitException();
	}

	public void directoryCommand() {
		File[] files = shellPath.listFiles();
		for (File file : files) {
			println(file.getName());
		}
	}

	public void printWorkingDirectoryCommand() {
		println(shellPath.getAbsolutePath());
	}

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

	public void copyCommand(String sourceFileOrDirectoryName, String destinationDirectoryName) {
//		File sourceFileOrDirecotry = new File(sourceFileOrDirectoryName);
//		File destinationFile = new File(destinationDirectoryName + sourceFileOrDirectoryName);
//		try {
//			removeCommand(destinationDirectoryName);
//		} catch (Exception e) {
//			// do nothing
//		}
//		try {
//			destinationFile.createNewFile();
//		} catch (IOException e) {
//
//		}
	}
}
