package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;
import java.io.IOException;

public class CommandMakeDirectory extends AbstractCommand {
	private StateShell state;
	public CommandMakeDirectory(StateShell st) {
		this.state = st;
		setNumberOfArguments(0);
	}
	public void execute(String[] args) throws IOException {
		File f = new File(state.currentDir, args[1]);
		if (!f.exists()) {
			f.mkdir();
		} else {
			throw new IOException("mkdir: '" + args[1] +"' already exist");
		}
	}
	public String getName(){
		return "mkdir";
	}
}
