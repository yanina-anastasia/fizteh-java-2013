package ru.fizteh.fivt.students.baldindima.filemap;

import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

public class ShellDbPut extends ShellIsItCommand {
	private DataBase dataBase;
	public ShellDbPut(final DataBase dBase){
		setName("put");
		setNumberOfArgs(3);
		dataBase = dBase;
	}
	
	public void run(){
		String newString = dataBase.put(arguments[1], arguments[2]);
		if (newString == null){
			System.out.println("new");
		} else {
			System.out.println("overwrite");
			System.out.println(newString);
		}
	}

}
