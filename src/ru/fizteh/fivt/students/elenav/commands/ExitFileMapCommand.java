package ru.fizteh.fivt.students.elenav.commands;

import java.io.IOException;
import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.filemap.FileMapState;
import ru.fizteh.fivt.students.elenav.shell.FilesystemState;

public class ExitFileMapCommand extends AbstractCommand {
	public ExitFileMapCommand(FilesystemState s) {
		super(s, "exit", 0);
	}
		
	public void execute(String[] args, PrintStream s) throws IOException {
		((FileMapState) getState()).writeFile();
		System.exit(0);
	}
	
}
