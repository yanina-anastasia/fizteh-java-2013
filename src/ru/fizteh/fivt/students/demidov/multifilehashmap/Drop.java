package ru.fizteh.fivt.students.demidov.multifilehashmap;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public class Drop extends BasicMultiFileHashMapCommand {
	public Drop(BasicDataBaseState dataBaseState) {
		super(dataBaseState);
	}
	public void executeCommand(String[] arguments, Shell usedShell) throws IOException {  
		dataBaseState.drop(arguments[0]);
		usedShell.curShell.getOutStream().println("dropped");
	}	
	public int getNumberOfArguments() {
		return 1;
	}	
	public String getCommandName() {
		return "drop";
	}
}