package ru.fizteh.fivt.students.baldindima.shell;

import java.io.IOException;

public class ShellPwd implements ShellCommand{
	private String name = "pwd";
	private FileFunctions fileFunctions;
	public ShellPwd(final FileFunctions newFileFunctions){
		fileFunctions = newFileFunctions;
	}
	public boolean isItCommand(final String[] commands) throws IOException{
		if (commands[0].equals(name)){
		if (commands.length != 1){
			throw new IOException("Invalid number of arguments");
		
		}
		return true;
		}
		return false;
	}
	public void run() throws IOException{
		System.out.println(fileFunctions.getCurrentDir());
	}
	

}
