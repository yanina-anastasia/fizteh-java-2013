package ru.fizteh.fivt.students.irinapodorozhnaya.utils;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;


public abstract class State {
	public InputStream in;
	public PrintStream out;
	public HashMap<String, Command> commands;
	public File currentDir;	
	
	public State() {
		in  = System.in;
		out = System.out;
		commands = new HashMap<String, Command>();
	}
	
	public void add (Command com) {
		commands.put(com.getName(), com);
	}
	
}
