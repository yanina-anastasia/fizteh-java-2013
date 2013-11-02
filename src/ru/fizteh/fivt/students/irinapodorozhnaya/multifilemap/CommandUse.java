package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap;

import java.io.IOException;

import ru.fizteh.fivt.students.irinapodorozhnaya.shell.AbstractCommand;

public class CommandUse extends AbstractCommand{
	private MultiFileMapState state;
	
	public CommandUse(MultiFileMapState st) {
		super(1);
		state = st;
	}
	
	public String getName() {
		return "use";
	}
	
	public void execute(String[] args) throws IOException {		
		state.commitDif();		
		try {
			state.setWorkingTable(new Table(args[1], state.getCurrentDir()));
			state.getOutputStream().println("using " + args[1]);
		} catch (IOException e) {
			state.getOutputStream().println(args[1] + " not exists");
		}
	}
}