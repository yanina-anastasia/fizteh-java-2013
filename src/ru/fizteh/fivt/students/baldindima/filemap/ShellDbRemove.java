package ru.fizteh.fivt.students.baldindima.filemap;

import java.io.IOException;

import ru.fizteh.fivt.students.baldindima.shell.ShellCommand;

public class ShellDbRemove implements ShellCommand {
	private String name = "remove";
	private DataBase dataBase;
	private String[] arguments;
	public ShellDbRemove(final DataBase dBase){
		dataBase = dBase;
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
		String newString = dataBase.remove(arguments[1]);
		if (newString == null){
			System.out.println("not found");
		} else {
			System.out.println("removed");
			
		}
	}

}

