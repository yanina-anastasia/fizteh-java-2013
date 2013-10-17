package ru.fizteh.fivt.students.msandrikova.filemap;

import java.io.File;

import ru.fizteh.fivt.students.msandrikova.shell.Command;

public class GetCommand extends Command {

	public GetCommand() {
		super("get", 1);
	}

	@Override
	public File execute(String[] argumentsList, boolean isInteractive, File currentDirectory) {
		if(!super.getArgsAcceptor(argumentsList.length - 1, isInteractive)) {
			return currentDirectory;
		}
		DBMap myDBMap = new DBMap(currentDirectory, isInteractive);
		String value;
		if((value = myDBMap.get(argumentsList[1])) == null){
			System.out.println("not found");
		} else {
			System.out.println("found");
			System.out.println(value);
		}
		myDBMap.writeFile();
		return currentDirectory;
	}

}
