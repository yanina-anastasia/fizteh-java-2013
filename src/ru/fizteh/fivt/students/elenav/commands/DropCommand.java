package ru.fizteh.fivt.students.elenav.commands;

import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.states.FilesystemState;
import ru.fizteh.fivt.students.elenav.states.MonoMultiAbstractState;

public class DropCommand extends AbstractCommand {

	public DropCommand(FilesystemState s) {
		super(s, "drop", 1);
	}

	public void execute(String[] args, PrintStream s) {
		try {
			MonoMultiAbstractState multi = (MonoMultiAbstractState) getState();
			multi.provider.removeTable(args[1]);
			if (multi.getWorkingTable() != null && multi.getWorkingTable().getName().equals(args[1])) {
				multi.setWorkingTable(null);
			}
			getState().getStream().println("dropped");
		} catch (IllegalStateException e) {
			getState().getStream().println(args[1] + "not exists");
		}
	}

}
