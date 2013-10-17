package ru.fizteh.fivt.students.inaumov.filemap;

import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		FileMapShell fileMapShell = new FileMapShell();
		FileMapState fileMapState = new FileMapState();
		
		String directory = System.getProperty("fizteh.db.dir");
		
		try {
			fileMapState.table = new SingleFileTable(directory, "DataBase");
		} catch (IOException exception) {
			System.err.println(exception.getMessage());
			System.exit(1);
		} catch (IncorrectArgumentsException exception) {
			System.err.println(exception.getMessage());
			System.exit(1);
		}
		
		fileMapShell.setFileMapState(fileMapState);
		
		fileMapShell.addCommand(new PutCommand());
		fileMapShell.addCommand(new GetCommand());
		fileMapShell.addCommand(new RemoveCommand());
		fileMapShell.addCommand(new ExitCommand());
		fileMapShell.addCommand(new CommitCommand());
		fileMapShell.addCommand(new RollbackCommand());
		fileMapShell.addCommand(new SizeCommand());
		
		fileMapShell.interactiveMode();
	}
}
