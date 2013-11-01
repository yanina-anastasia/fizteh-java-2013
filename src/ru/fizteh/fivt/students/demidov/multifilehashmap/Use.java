package ru.fizteh.fivt.students.demidov.multifilehashmap;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.shell.Shell;


public class Use extends BasicMultiFileHashMapCommand {
	public Use(MultiFileMap usedMultiFileMap) {
		super(usedMultiFileMap);
	}
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {    
		multiFileMap.changeUsedFilesMap(arguments[0]);
		usedShell.curShell.getOutStream().println("using " + arguments[0]);
	}	
	public int getNumberOfArguments() {
		return 1;
	}	
	public String getCommandName() {
		return "use";
	}
}