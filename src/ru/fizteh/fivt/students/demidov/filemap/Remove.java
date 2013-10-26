package ru.fizteh.fivt.students.demidov.filemap;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Remove extends BasicFileMapCommand {
	public Remove(FileMapState currentFileMapState) {
		super(currentFileMapState);
	}
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {    
		String value = fileMapState.getCurrentFileMap(arguments[0]).getCurrentTable().remove(arguments[0]);
		if (value == null) {
			usedShell.curShell.getOutStream().println("not found");
		} else {
			usedShell.curShell.getOutStream().println("removed");
		}
	}	
	public int getNumberOfArguments() {
		return 1;
	}	
	public String getCommandName() {
		return "remove";
	}
}