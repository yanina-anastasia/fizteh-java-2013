package ru.fizteh.fivt.students.demidov.shell;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Mv implements BasicCommand {
	public void executeCommand(String[] arguments, Shell.CurrentShell curShell) throws IOException {	
		File source = Utils.getFile(arguments[0], curShell);
		File destination = Utils.getFile(arguments[1], curShell);
		
		if ((source.exists()) && (source.getPath().equals(destination.getPath()))) {
			return;
		}
		
		if ((source.exists()) && (source.isDirectory()) && (!destination.exists()) && (source.getParent().equals(destination.getParent()))) {
			if (!source.renameTo(destination)) {
				throw new IOException("unable to move " + source.getPath() + " to " + destination.getPath());
			}
			return;
		}
		
		(new Cp()).executeCommand(arguments, curShell);
		(new Rm()).executeCommand(Arrays.copyOfRange(arguments, 0, 1), curShell);
	}
}
