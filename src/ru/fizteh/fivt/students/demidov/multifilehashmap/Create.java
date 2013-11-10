package ru.fizteh.fivt.students.demidov.multifilehashmap;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.shell.Shell;


public class Create extends BasicMultiFileHashMapCommand {
	public Create(BasicDataBaseState dataBaseState) {
		super(dataBaseState);
	}
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {    
		dataBaseState.create(arguments[0]);
		usedShell.curShell.getOutStream().println("created");
	}	
	public int getNumberOfArguments() {
		return 1;
	}	
	public String getCommandName() {
		return "create";
	}
}