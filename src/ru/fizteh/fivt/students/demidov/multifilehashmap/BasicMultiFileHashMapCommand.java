package ru.fizteh.fivt.students.demidov.multifilehashmap;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.shell.BasicCommand;
import ru.fizteh.fivt.students.demidov.shell.Shell;
import ru.fizteh.fivt.students.demidov.shell.ShellInterruptionException;

public abstract class BasicMultiFileHashMapCommand implements BasicCommand {
	public BasicMultiFileHashMapCommand(MultiFileMap usedMultiFileMap) {
		multiFileMap = usedMultiFileMap;
	}
	
	abstract public void executeCommand(String[] arguments, Shell usedShell) throws IOException, ShellInterruptionException;
	abstract public int getNumberOfArguments();
	abstract public String getCommandName();
	
	protected MultiFileMap multiFileMap;
}