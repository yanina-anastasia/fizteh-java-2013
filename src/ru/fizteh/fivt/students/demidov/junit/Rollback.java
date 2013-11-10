package ru.fizteh.fivt.students.demidov.junit;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.multifilehashmap.BasicMultiFileHashMapCommand;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Rollback extends BasicMultiFileHashMapCommand {
	public Rollback(DataBaseState dataBaseState) {
		super(dataBaseState);
	}	
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {    
		usedShell.curShell.getOutStream().println(dataBaseState.getUsedTable().rollback());
	}	
	public int getNumberOfArguments() {
		return 0;
	}	
	public String getCommandName() {
		return "rollback";
	}
}