package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.IOException;

public class CommandExit extends AbstractCommand {
	private StateShell state;
	public CommandExit(StateShell st) {
		state = st;
		argsNumber = 0;
	}
	public String getName(){
		return "exit";
	}
	public void execute(String[] args) throws IOException{
		System.exit(0);
	}
}
