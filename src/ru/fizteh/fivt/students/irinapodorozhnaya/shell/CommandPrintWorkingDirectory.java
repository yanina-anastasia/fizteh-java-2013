package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.IOException;

public class CommandPrintWorkingDirectory extends AbstractCommand {	
	private StateShell state;
	private int argsNumber;
	public CommandPrintWorkingDirectory(StateShell st) {
		this.state = st;
		argsNumber = 0;
	}
	public String getName(){
		return "pwd";
	}
	public void execute(String[] args) throws IOException {
		state.out.println(state.currentDir.getCanonicalPath());
	}
}
