package ru.fizteh.fivt.students.demidov.shell;

import java.io.File;

public class Dir implements BasicCommand {
	public void executeCommand(String[] arguments, Shell.CurrentShell curShell) {    
		for (String currentFile : (new File(curShell.getCurrentDirectory())).list()) {
			System.out.println(currentFile);
		}
	}
}
