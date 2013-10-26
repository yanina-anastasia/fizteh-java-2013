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
		File table = new File(absolutePath(args[1]));
		if (!table.exists()) {
			getState().getStream().print(args[1] + "not exists");
		} else {
			MultiFileMapState multi = (MultiFileMapState) getState();
			if (!multi.equals(null)) {
				multi.write();
			}
			multi.setWorkingTable(((MultiFileMapState) getState()).getTables().get(args[1]));
			multi.read();
			getState().getStream().print("using" + args[1]);
		}
	}

}
