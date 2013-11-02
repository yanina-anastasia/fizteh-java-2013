package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.shell.AbstractCommand;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.CommandRemove;

public class CommandDrop extends AbstractCommand{
	
	private MultiFileMapState state;
	
	public CommandDrop(MultiFileMapState st) {
		super(1);
		state = st;
	}
	
	public String getName() {
		return "drop";
	}
	
	public void execute(String[] args) throws IOException {
		File tableDir = new File(state.getCurrentDir(), args[1]);
		if (!tableDir.exists()) {
			state.getOutputStream().println(args[1] + " not exists");
		} else {
			CommandRemove.DeleteRecursivly(tableDir);
			state.getOutputStream().println("dropped");
		}
	}
}