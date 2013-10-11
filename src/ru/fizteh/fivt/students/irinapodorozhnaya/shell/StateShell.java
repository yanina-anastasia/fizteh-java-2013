package ru.fizteh.fivt.students.irinapodorozhnaya.shell;
import java.io.File;

import ru.fizteh.fivt.students.irinapodorozhnaya.utils.State;

public class StateShell extends State {
	
	public StateShell() {
		currentDir = new File(".");
		add(new CommandDirectory(this));
		add(new CommandChangeDirectory(this));
		add(new CommandRemove(this));
		add(new CommandMove(this));
		add(new CommandPrintWorkingDirectory(this));
		add(new CommandCopy(this));
		add(new CommandExit(this));
		add(new CommandMakeDirectory(this));
	}
}