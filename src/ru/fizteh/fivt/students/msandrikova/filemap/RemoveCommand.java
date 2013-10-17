package ru.fizteh.fivt.students.msandrikova.filemap;

import java.io.File;

import ru.fizteh.fivt.students.msandrikova.shell.Command;

public class RemoveCommand extends Command {

	public RemoveCommand() {
		super("remove", 1);
	}

	@Override
	public File execute(String[] argumentsList, boolean isInteractive, File currentDirectory) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, isInteractive)) {
			return currentDirectory;
		}
		DBMap myDBMap = new DBMap(currentDirectory, isInteractive);
		if(myDBMap.remove(argumentsList[1]) == null){
			System.out.println("not found");
		} else {
			System.out.println("removed");
		}
		myDBMap.writeFile();
		return currentDirectory;
	}

}
