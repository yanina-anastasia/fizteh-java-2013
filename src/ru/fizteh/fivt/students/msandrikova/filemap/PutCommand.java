package ru.fizteh.fivt.students.msandrikova.filemap;

import java.io.File;

import ru.fizteh.fivt.students.msandrikova.shell.Command;

public class PutCommand extends Command {

	public PutCommand() {
		super("put", 2);
	}

	@Override
	public File execute(String[] argumentsList, boolean isInteractive, File currentDirectory) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, isInteractive)) {
			return currentDirectory;
		}
		DBMap myDBMap = new DBMap(currentDirectory, isInteractive);
		String oldValue;
		if((oldValue = myDBMap.put(argumentsList[1], argumentsList[2])) == null){
			System.out.println("new");
		} else {
			System.out.println("overwrite");
			System.out.println(oldValue);
		}
		myDBMap.writeFile();
		return currentDirectory;
	}

}
