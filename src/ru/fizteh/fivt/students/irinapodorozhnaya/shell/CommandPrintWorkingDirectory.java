package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.IOException;

public class CommandPrintWorkingDirectory extends AbstractCommand {	
	private StateShell state;
	public CommandPrintWorkingDirectory(StateShell st) {
		this.state = st;
		setNumberOfArguments(0);
	}
	public String getName(){
		return "pwd";
	}
	public void execute(String[] args) throws IOException {
		state.out.println(state.currentDir.getCanonicalPath());
	}
}
