package ru.fizteh.fivt.students.demidov.multifilehashmap;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.filemap.Put;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class MultiPut extends BasicMultiFileHashMapCommand {
	public MultiPut(MultiFileMap usedMultiFileMap) {
		super(usedMultiFileMap);
	}
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {    
		new Put(multiFileMap.getFilesMap().getFileMapForKey(arguments[0])).executeCommand(arguments, usedShell);
	}	
	public int getNumberOfArguments() {
		return 2;
	}	
	public String getCommandName() {
		return "put";
	}
}