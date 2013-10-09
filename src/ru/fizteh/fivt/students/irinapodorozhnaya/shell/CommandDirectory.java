package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;

public class CommandDirectory extends AbstractCommand {
	CommandDirectory (StateShell st) {
		setState(st);
		setNumberOfArguments(0);
	}
	public void execute(String[] args) {
		File[] filesList = getState().currentDir.listFiles();
		for (File s: filesList) {
			getState().out.println(s.getName());
		}
	}
	public String getName() {
		return "dir";
	}
}	
