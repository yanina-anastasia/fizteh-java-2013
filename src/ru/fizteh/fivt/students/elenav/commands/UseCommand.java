package ru.fizteh.fivt.students.elenav.commands;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.filemap.FileMapState;
import ru.fizteh.fivt.students.elenav.multifilemap.MultiFileMapState;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class UseCommand extends AbstractCommand {

	public UseCommand(FilesystemState s) {
		super(s, "use", 1);
	}

	public void execute(String[] args, PrintStream s) throws IOException {
		File f = new File(getState().getWorkingDirectory(), args[1]);
		if (!f.exists()) {
			getState().getStream().println(args[1] + " not exists");
		} else {
			if (!args[1].equals(getState().getWorkingDirectory().getName())) {
				MultiFileMapState multi = (MultiFileMapState) getState();
				if (multi.getWorkingTable() != null) {
					multi.write();
				}
				multi.setWorkingTable(new FileMapState(args[1], f, getState().getStream()));
				multi.read();
				getState().getStream().println("using " + args[1]);
			}
		}
	}

}
