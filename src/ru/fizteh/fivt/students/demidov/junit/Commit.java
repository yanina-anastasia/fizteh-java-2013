package ru.fizteh.fivt.students.demidov.junit;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.multifilehashmap.BasicMultiFileHashMapCommand;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Commit extends BasicMultiFileHashMapCommand {
	public Commit(DataBaseState dataBaseState) {
		super(dataBaseState);
	}	
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {
		usedShell.curShell.getOutStream().println(dataBaseState.getUsedTable().commit());
	}	
	public int getNumberOfArguments() {
		return 0;
	}	
	public String getCommandName() {
		return "commit";                                                                                                          
	}
}