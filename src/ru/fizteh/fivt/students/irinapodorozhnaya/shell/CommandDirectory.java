package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;

public class CommandDirectory extends AbstractCommand {
	private StateShell state;
	CommandDirectory (StateShell st) {
		state = st;
		setNumberOfArguments(0);
	}
	public void execute(String[] args) {
		File[] filesList = state.currentDir.listFiles();
		for (File s: filesList) {
			state.out.println(s.getName());
		}
	}
	public String getName() {
		return "dir";
	}
}	
