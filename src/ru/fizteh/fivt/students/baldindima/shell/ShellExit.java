package ru.fizteh.fivt.students.baldindima.shell;

import java.io.IOException;

import ru.fizteh.fivt.students.baldindima.filemap.DataBase;

public class ShellExit implements ShellCommand {
	private String name = "exit";
	private DataBase dataBase;
	private String[] arguments;
	public ShellExit(){
		
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
	public void run() throws  ExitException{
		throw new ExitException ();
	}

}
