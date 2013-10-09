package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Vector;

public class StateShell {
	InputStream in;
	PrintStream out;
	File currentDir;	
	Vector <AbstractCommand> commands;
	public StateShell() {
		in  = System.in;
		out = System.out;
		currentDir = new File(".");
		commands = new Vector<AbstractCommand>();
		commands.add(new CommandDirectory(this));
		commands.add(new CommandChangeDirectory(this));
		commands.add(new CommandRemove(this));
		commands.add(new CommandMove(this));
		commands.add(new CommandPrintWorkingDirectory(this));
		commands.add(new CommandCopy(this));
		commands.add(new CommandExit(this));
		commands.add(new CommandMakeDirectory(this));
	}
}