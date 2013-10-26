package ru.fizteh.fivt.students.baldindima.filemap;



import ru.fizteh.fivt.students.baldindima.shell.ShellIsItCommand;

public class ShellDbRemove extends ShellIsItCommand {
	private DataBaseTable dataBaseTable;
	public ShellDbRemove(final DataBaseTable dBaseTable){
		setName("remove");
		setNumberOfArgs(2);
		dataBaseTable = dBaseTable;
	}
	
	
	public void run() {
		if (!dataBaseTable.exists()){
			System.out.println("no table");
			return;
		}
		String newString = dataBaseTable.remove(arguments[1]);
		if (newString == null){
			System.out.println("not found");
		} else {
			System.out.println("removed");
			
		}
	}

}

