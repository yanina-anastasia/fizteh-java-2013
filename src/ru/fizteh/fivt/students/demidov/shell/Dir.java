package ru.fizteh.fivt.students.demidov.shell;

import java.io.File;

public class Dir implements BasicCommand {
	public void executeCommand(String[] arguments) {    
		for (String currentFile : (new File(Shell.getCurrentDirectory())).list()) {
			System.out.println(currentFile);
		}
	}
}
