package ru.fizteh.fivt.students.demidov.multifilehashmap;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Drop extends BasicMultiFileHashMapCommand {
	public Drop(MultiFileMap usedMultiFileMap) {
		super(usedMultiFileMap);
	}
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {    
		multiFileMap.deleteTable(arguments[0]);
		usedShell.curShell.getOutStream().println("dropped");
	}	
	public int getNumberOfArguments() {
		return 1;
	}	
	public String getCommandName() {
		return "drop";
	}
}