package ru.fizteh.fivt.students.baldindima.shell;

import java.io.IOException;

public class ShellMkdir implements ShellCommand{
	private String name = "mkdir";
	private FileFunctions fileFunctions;
	private String[] arguments;
	public ShellMkdir(final FileFunctions newFileFunctions){
		fileFunctions = newFileFunctions;
	}
	public boolean isItCommand(final String[] commands) throws IOException{
		if (commands[0].equals(name)){
		if (commands.length != 2){
			throw new IOException("Invalid number of arguments");
		
		}
		arguments = commands;
		return true;
		}
		return false;
	}
	public void run() throws IOException{
		FileFunctions.createDir(arguments[1]);
	}

}
