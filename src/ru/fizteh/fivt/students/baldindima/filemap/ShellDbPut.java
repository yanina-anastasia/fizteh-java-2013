package ru.fizteh.fivt.students.baldindima.filemap;

import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

public class ShellDbPut extends ShellIsItCommand {
	private DataBaseTable dataBaseTable;
	public ShellDbPut(final DataBaseTable dBaseTable){
		setName("put");
		setNumberOfArgs(3);
		dataBaseTable = dBaseTable;
	}
	
	public void run(){
		if (!dataBaseTable.exists()){
			System.out.println("no table");
			return;
		}
		String newString = dataBaseTable.put(arguments[1], arguments[2]);
		if (newString == null){
			System.out.println("new");
		} else {
			System.out.println("overwrite");
			System.out.println(newString);
		}
	}

}
