package ru.fizteh.fivt.students.elenav.commands;

import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.states.FilesystemState;
import ru.fizteh.fivt.students.elenav.states.MonoMultiAbstractState;

public class GetCommand extends AbstractCommand {
	public GetCommand(FilesystemState s) {
		super(s, "get", 1);
	}
	
	public void execute(String[] args, PrintStream s) {
		MonoMultiAbstractState fileMap = (MonoMultiAbstractState) getState();
		if (fileMap.getWorkingDirectory() == null) {
			getState().getStream().println("no table");
		} else {
			String value = fileMap.get(args[1]);
			if (value != null) {
				getState().getStream().println("found");
				getState().getStream().println(value);
			} else {
				getState().getStream().println("not found");
			}
		}
	}
}
