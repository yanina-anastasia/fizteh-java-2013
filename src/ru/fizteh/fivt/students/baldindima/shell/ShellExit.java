package ru.fizteh.fivt.students.baldindima.shell;

import java.io.IOException;

public class ShellExit implements ShellCommand {
	private String name = "exit";
	private FileFunctions fileFunctions;
	private String[] arguments;
	public ShellExit(final FileFunctions newFileFunctions){
		fileFunctions = newFileFunctions;
	}
	public boolean isItCommand(final String[] commands) throws IOException{
		if (commands[0].equals(name)){
		if (commands.length != 1){
			throw new IOException("Invalid number of arguments");
		
		}
		arguments = commands;
		return true;
		}
		return false;
	}
	public void run() throws IOException{
		System.exit(0);
	}

}
