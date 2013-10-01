package ru.fizteh.fivt.students.mishatkin.shell;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * CommandReceiver.java
 * Created by Vladimir Mishatkin on 9/26/13
 */
public interface CommandReceiver {
	public void changeDirectoryCommand(String arg) throws FileNotFoundException;
	public void directoryCommand();
	public void printWorkingDirectoryCommand();
	public void makeDirectoryCommand(String arg);
	public void removeCommand(String arg) throws IOException;
	public void copyCommand(String sourceFileOrDirectoryName, String destinationDirectoryName) throws Exception;
	public void moveCommand(String sourceFileOrDirectoryName, String destinationFileOrDirectoryName) throws Exception;
	public void exitCommand() throws TimeToExitException;
}
