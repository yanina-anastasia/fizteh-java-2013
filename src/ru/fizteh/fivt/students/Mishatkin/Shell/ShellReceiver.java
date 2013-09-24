package ru.fizteh.fivt.students.Mishatkin.Shell;

import javafx.scene.effect.ReflectionBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.regex.Pattern;

/**
 * ShellReceiver.java
 * Created by Vladimir Mishatkin on 9/24/13
 *
 */

public class ShellReceiver {
	private static ShellReceiver sharedInstance = null;
	private PrintWriter out;
	private File shellPath;

	private ShellReceiver() {
		shellPath = File.listRoots()[0];
		out = Shell.initialOutput;
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
		if (out != null) {
			System.out.print(shellPath.getAbsolutePath() + " " + simplePrompt() + " ");
		}
	}

	public void changeDirectory(String arg) throws FileNotFoundException {
		String previousStatePath = shellPath.getAbsolutePath();
		if (arg.charAt(0) == File.separatorChar) {
			while (shellPath.getParent() != null) {
				shellPath = shellPath.getParentFile();
			}
		}
		String separatorRegularExpression = (File.separator.equals("/")) ? File.separator : "\\\\";
		String[] sequence = arg.split(separatorRegularExpression);
		for (String simpleArg : sequence) {

			try {
				simpleChangeDirectory(simpleArg);
			} catch (FileNotFoundException e) {
				//  reverse transaction sequence
				shellPath = new File(previousStatePath);
				throw (e);
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
}
