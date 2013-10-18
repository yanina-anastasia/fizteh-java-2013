package ru.fizteh.fivt.students.mishatkin.shell;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * CommandReceiver.java
 * Created by Vladimir Mishatkin on 9/26/13
 */
public interface CommandReceiver {
	public void changeDirectoryCommand(String arg) throws ShellException;
	public void directoryCommand();
	public void printWorkingDirectoryCommand();
	public void makeDirectoryCommand(String arg) throws ShellException;
	public void removeCommand(String arg) throws ShellException;
	public void copyCommand(String sourceFileOrDirectoryName, String destinationDirectoryName) throws ShellException;
	public void moveCommand(String sourceFileOrDirectoryName, String destinationFileOrDirectoryName) throws ShellException;
	public void exitCommand() throws TimeToExitException;
}
