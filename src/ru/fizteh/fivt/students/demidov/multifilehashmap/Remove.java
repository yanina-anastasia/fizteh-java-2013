package ru.fizteh.fivt.students.demidov.multifilehashmap;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Remove extends BasicMultiFileHashMapCommand {
	public Remove(MultiFileMap usedMultiFileMap) {
		super(usedMultiFileMap);
	}
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {    
		String value = multiFileMap.getFileMap().getCurrentTable().remove(arguments[0]);
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