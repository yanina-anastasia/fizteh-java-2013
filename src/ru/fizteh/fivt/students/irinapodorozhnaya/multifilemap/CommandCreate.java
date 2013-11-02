package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.shell.AbstractCommand;

public class CommandCreate extends AbstractCommand{
	private MultiFileMapState state;
	
	public CommandCreate(MultiFileMapState st) {
		super(1);
		state = st;
	}
	
	public String getName() {
		return "create";
	}
	
	public void execute(String[] args) throws IOException {
		File tableDir = new File(state.getCurrentDir(), args[1]);
		if (tableDir.exists()) {
			state.getOutputStream().println(args[1] + " exists");
		} else {
			if (!tableDir.mkdir()) {
				throw new IOException("can't create directory");
			}
			state.getOutputStream().println("created");
		}
	}
}
