package ru.fizteh.fivt.students.irinapodorozhnaya.utils;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;


public abstract class State {
	private InputStream in;
	private PrintStream out;
	private final HashMap<String, Command> commands;
	private File currentDir;	
	
	public State() {
		setInputStream(System.in);
		setOutputStream(System.out);
		commands = new HashMap<String, Command>();
	}
	
	public void add (Command com) {
		getCommands().put(com.getName(), com);
	}

	public InputStream getInputStream() {
		return in;
	}

	public void setInputStream(InputStream in) {
		this.in = in;
	}

	public PrintStream getOutputStream() {
		return out;
	}

	public void setOutputStream(PrintStream out) {
		this.out = out;
	}

	public File getCurrentDir() {
		return currentDir;
	}

	public void setCurrentDir(File currentDir) throws IOException {
		if (!currentDir.exists()) {
			throw new IOException("no such directory");
		}
		this.currentDir = currentDir;
	}

	public HashMap<String, Command> getCommands() {
		return commands;
	}
}
