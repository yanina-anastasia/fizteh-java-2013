package ru.fizteh.fivt.students.demidov.shell;

import java.io.IOException;

public class Cp implements BasicCommand {
	public void executeCommand(String[] arguments) throws IOException {	
		Utils.copyFileOrDirectory(Utils.getFile(arguments[0]), Utils.getFile(arguments[1]));
	}
}
