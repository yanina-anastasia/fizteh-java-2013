package ru.fizteh.fivt.students.elenav.commands;

import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.states.MonoMultiAbstractState;

public class RemoveCommand extends AbstractCommand {

	public RemoveCommand(MonoMultiAbstractState s) {
		super(s, "remove", 1);
	}

	public void execute(String[] args, PrintStream s) {
		MonoMultiAbstractState currentState = (MonoMultiAbstractState) getState();
		if (currentState.getWorkingDirectory() == null) {
			getState().getStream().println("no table");
		} else {
			String result = currentState.remove(args[1]);
			if (result != null) {
				getState().getStream().println("removed");
				getState().getStream().println(result);
			}
			else {
				getState().getStream().println("not found");
			}
		}
	}
	
}
