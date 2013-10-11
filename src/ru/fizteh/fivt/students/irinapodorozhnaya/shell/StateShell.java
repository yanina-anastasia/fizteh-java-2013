package ru.fizteh.fivt.students.irinapodorozhnaya.shell;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;

public class StateShell {
	InputStream in;
	PrintStream out;
	File currentDir;	
	HashMap<String, Command> commands;
	public StateShell() {
		in  = System.in;
		out = System.out;
		currentDir = new File(".");
		commands = new HashMap<String, Command>();
		commands.put("dir", new CommandDirectory(this));
		commands.put("cd",  new CommandChangeDirectory(this));
		commands.put("rm",  new CommandRemove(this));
		commands.put("mv",  new CommandMove(this));
		commands.put("pwd", new CommandPrintWorkingDirectory(this));
		commands.put("cp",  new CommandCopy(this));
		commands.put("exit",  new CommandExit(this));
		commands.put("mkdir",  new CommandMakeDirectory(this));
	}
}