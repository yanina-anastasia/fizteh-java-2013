package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;


public class CommandDirectory extends AbstractCommand {
	CommandDirectory (StateShell state) {
		super(0, state);
	}
	
	public void execute(String[] args) {
		File[] filesList = getState().getCurrentDir().listFiles();
		for (File s: filesList) {
			getState().getOutputStream().println(s.getName());
		}
	}
	
	public String getName() {
		return "dir";
	}
}	
