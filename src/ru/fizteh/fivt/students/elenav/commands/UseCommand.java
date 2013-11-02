package ru.fizteh.fivt.students.elenav.commands;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.multifilemap.MultiFileMapState;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class UseCommand extends AbstractCommand {

	public UseCommand(FilesystemState s) {
		super(s, "use", 1);
	}

	public void execute(String[] args, PrintStream s) throws IOException {
		MultiFileMapState multi = (MultiFileMapState) getState();
		if (multi.getWorkingTable() != null) {
			int numberOfChanges = multi.getNumberOfChanges();
			if (numberOfChanges != 0) {
				s.println(numberOfChanges + " unsaved changes");
			} else {
				useTable(args[1]);
			}
		} else {
			useTable(args[1]);
		}
	}
	
	private void useTable(String name) throws IOException {
		File f = new File(getState().getWorkingDirectory(), name);
		if (!f.exists()) {
			getState().getStream().println(name + " not exists");
		} else {
			if (!name.equals(getState().getWorkingDirectory().getName())) {
				MultiFileMapState multi = (MultiFileMapState) getState();
				multi.setWorkingTable(new MultiFileMapState(name, f, getState().getStream()));
				multi.read();
				getState().getStream().println("using " + name);
			}
		}
	}

}
