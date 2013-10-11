package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.utils.AbstractCommand;

public class CommandPrintWorkingDirectory extends AbstractCommand {	
	public CommandPrintWorkingDirectory(StateShell st) {
		super(0, st);;
	}
	
	public String getName() {
		return "pwd";
	}
	
	public void execute(String[] args) throws IOException {
		getState().out.println(getState().currentDir.getCanonicalPath());
	}
}
