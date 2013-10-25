package ru.fizteh.fivt.students.baldindima.filemap;



import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

public class ShellDbRemove extends ShellIsItCommand {
	private DataBase dataBase;
	public ShellDbRemove(final DataBase dBase){
		setName("remove");
		setNumberOfArgs(2);
		dataBase = dBase;
	}
	
	
	public void run() {
		String newString = dataBase.remove(arguments[1]);
		if (newString == null){
			System.out.println("not found");
		} else {
			System.out.println("removed");
			
		}
	}

}

