package ru.fizteh.fivt.students.baldindima.shell;

import java.io.File;
import java.io.IOException;

public class ShellMv implements ShellCommand {
	private String name = "mv";
	private FileFunctions fileFunctions;
	private String[] arguments;
	public ShellMv(final FileFunctions newFileFunctions){
		fileFunctions = newFileFunctions;
	}
	public boolean isItCommand(final String[] commands) throws IOException{
		if (commands[0].equals(name)){
		if (commands.length != 3){
			throw new IOException("Invalid number of arguments");
		
		}
		arguments = commands;
		return true;
		}
		return false;
	}
	public void run() throws IOException{
		FileFunctions.move(arguments);
		
	}


}
