package ru.fizteh.fivt.students.demidov.junit;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.multifilehashmap.BasicMultiFileHashMapCommand;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Size extends BasicMultiFileHashMapCommand {
	public Size(DataBaseState dataBaseState) {
		super(dataBaseState);
	}	
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {    
		usedShell.curShell.getOutStream().println(dataBaseState.getUsedTable().size());
	}	
	public int getNumberOfArguments() {
		return 0;
	}	
	public String getCommandName() {
		return "size";
	}
}