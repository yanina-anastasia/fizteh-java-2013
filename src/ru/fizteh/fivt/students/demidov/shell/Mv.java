package ru.fizteh.fivt.students.demidov.shell;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Mv implements BasicCommand {
	public void executeCommand(String[] arguments) throws IOException {	
		File source = Utils.getFile(arguments[0]);
		File destination = Utils.getFile(arguments[1]);
		if (source.getParent().equals(destination.getParent())) {
			(new Rm()).executeCommand(Arrays.copyOfRange(arguments, 1, 2));
			if (!source.renameTo(destination)) {
				throw new IOException("unable to move" + source.getPath() + " to " + destination.getPath());
			}
		} else {
			(new Cp()).executeCommand(arguments);
			(new Rm()).executeCommand(Arrays.copyOfRange(arguments, 0, 1));
		}
	}
}
