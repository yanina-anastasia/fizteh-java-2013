package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;

import ru.fizteh.fivt.students.irinapodorozhnaya.utils.AbstractCommand;

public class CommandDirectory extends AbstractCommand {
	CommandDirectory (StateShell state) {
		super(0, state);
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
