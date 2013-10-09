package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;

public class CommandDirectory extends AbstractCommand {
	private StateShell state;
	private int argsNumber;
	CommandDirectory (StateShell st) {
		state = st;
		argsNumber = 0;
	}
	public void execute(String[] args) {
		File[] filesList = state.currentDir.listFiles();
		for (File s: filesList) {
			System.out.println(s.getName());
		}
	}
	public String getName(){
		return "dir";
	}
}	
