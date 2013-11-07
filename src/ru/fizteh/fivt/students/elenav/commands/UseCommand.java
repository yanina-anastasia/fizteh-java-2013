package ru.fizteh.fivt.students.elenav.commands;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.multifilemap.MultiFileMapState;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;
import ru.fizteh.fivt.students.elenav.states.MonoMultiAbstractState;

public class UseCommand extends AbstractCommand {

	public UseCommand(FilesystemState s) {
		super(s, "use", 1);
	}

	public void execute(String[] args, PrintStream s) throws IOException {
		MultiFileMapState multi = (MultiFileMapState) getState();
		String name = args[1];
		if (multi.getWorkingDirectory() != null) {
			int numberOfChanges = multi.getNumberOfChanges();
			if (numberOfChanges != 0) {
				s.println(numberOfChanges + " unsaved changes");
			} else {
				useTable(name);
			}
		} else {
			useTable(name);
		}
	}
	
	private void useTable(String name) throws IOException {
		File f = new File(((MonoMultiAbstractState) getState()).provider.getWorkingDirectory(), name);
		if (!f.exists()) {
			getState().getStream().println(name + " not exists");
		} else {
			if (getState().getWorkingDirectory() == null || getState().getName() != null && !name.equals(getState().getName())) {
				MultiFileMapState multi = (MultiFileMapState) getState();
				multi.setWorkingDirectory(f);
				multi.read();
				getState().getStream().println("using " + name);
			}
		}
	}

}
