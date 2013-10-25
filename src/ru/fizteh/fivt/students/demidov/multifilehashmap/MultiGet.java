package ru.fizteh.fivt.students.demidov.multifilehashmap;

import java.io.IOException;

import ru.fizteh.fivt.students.demidov.filemap.Get;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class MultiGet extends BasicMultiFileHashMapCommand {
	public MultiGet(MultiFileMap usedMultiFileMap) {
		super(usedMultiFileMap);
	}
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {  
		new Get(multiFileMap.getFilesMap().getFileMapForKey(arguments[0])).executeCommand(arguments, usedShell);
	}	
	public int getNumberOfArguments() {
		return 1;
	}	
	public String getCommandName() {
		return "get";
	}
}
