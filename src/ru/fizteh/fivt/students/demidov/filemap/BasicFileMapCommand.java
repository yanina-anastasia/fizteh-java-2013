package ru.fizteh.fivt.students.demidov.filemap;

import java.io.IOException;
import ru.fizteh.fivt.students.demidov.shell.BasicCommand;
import ru.fizteh.fivt.students.demidov.shell.InterruptionException;
import ru.fizteh.fivt.students.demidov.shell.Shell;

public abstract class BasicFileMapCommand implements BasicCommand {
	public BasicFileMapCommand(FileMap usedFileMap) {
		fileMap = usedFileMap;
	}
	
	abstract public void executeCommand(String[] arguments, Shell usedShell) throws IOException, InterruptionException;
	abstract public int getNumberOfArguments();
	abstract public String getCommandName();
	
	protected FileMap fileMap;
}
