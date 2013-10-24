package ru.fizteh.fivt.students.baldindima.filemap;

import java.io.IOException;

import ru.fizteh.fivt.students.baldindima.shell.ShellCommand;

public class ShellDbPut implements ShellCommand {
	private String name = "put";
	private DataBase dataBase;
	private String[] arguments;
	public ShellDbPut(final DataBase dBase){
		dataBase = dBase;
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
		String newString = dataBase.put(arguments[1], arguments[2]);
		if (newString == null){
			System.out.println("new");
		} else {
			System.out.println("overwrite");
			System.out.println(newString);
		}
	}

}
