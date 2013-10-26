package ru.fizteh.fivt.students.demidov.multifilehashmap;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.filemap.Remove;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class MultiRemove extends BasicMultiFileHashMapCommand {
	public MultiRemove(MultiFileMap usedMultiFileMap) {
		super(usedMultiFileMap);
	}
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {    
		new Remove(multiFileMap.getFilesMap().getFileMapForKey(arguments[0])).executeCommand(arguments, usedShell);
	}	
	public int getNumberOfArguments() {
		return 1;
	}	
	public String getCommandName() {
		return "remove";
	}
}