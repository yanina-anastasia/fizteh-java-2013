package ru.fizteh.fivt.students.baldindima.shell;

import java.io.IOException;

public class ShellCp implements ShellCommand {
	private String name = "cp";
	private FileFunctions fileFunctions;
	private String[] arguments;
	public ShellCp(final FileFunctions newFileFunctions){
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
		FileFunctions.readyToCopy(arguments);
	}

}
