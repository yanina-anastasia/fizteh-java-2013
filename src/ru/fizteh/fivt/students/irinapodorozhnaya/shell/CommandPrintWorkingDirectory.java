package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.IOException;

public class CommandPrintWorkingDirectory extends AbstractCommand {	
	public CommandPrintWorkingDirectory(StateShell st) {
		setState(st);
		setNumberOfArguments(0);
	}
	public String getName(){
		return "pwd";
	}
	public void execute(String[] args) throws IOException {
		getState().out.println(getState().currentDir.getCanonicalPath());
	}
}
