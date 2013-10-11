package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.utils.AbstractCommand;

public class CommandExit extends AbstractCommand {
	public CommandExit(StateShell st) {
		super(0, st);
	}
	
	public String getName() {
		return "exit";
	}
	
	public void execute(String[] args) throws IOException {
		System.exit(0);
	}
}
