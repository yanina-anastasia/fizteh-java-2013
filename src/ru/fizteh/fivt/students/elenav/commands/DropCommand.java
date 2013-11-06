package ru.fizteh.fivt.students.elenav.commands;

import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.states.FilesystemState;
import ru.fizteh.fivt.students.elenav.states.MonoMultiAbstractState;

public class DropCommand extends AbstractCommand {

	public DropCommand(FilesystemState s) {
		super(s, "drop", 1);
	}

	public void execute(String[] args, PrintStream s) {
		String name = args[1];
		MonoMultiAbstractState multi = (MonoMultiAbstractState) getState();
		if (multi.provider.getTable(name) != null) {
			multi.provider.removeTable(name);
			if (multi.getWorkingDirectory() != null && multi.getWorkingDirectory().getName().equals(name)) {
				multi.setWorkingDirectory(null);
			}
			getState().getStream().println("dropped");
		} else {
			getState().getStream().println(name + " not exists");
		}
	}

}
