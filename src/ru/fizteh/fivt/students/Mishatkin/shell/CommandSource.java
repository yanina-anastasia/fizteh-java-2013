package ru.fizteh.fivt.students.mishatkin.shell;

import java.util.Collections;
import java.util.Vector;

/**
 * CommandSource.java
 * Created by Vladimir Mishatkin on 9/23/13
 */
public abstract class CommandSource {
	private Vector<String> commandsStringsBuffer = new Vector<String>();
	private Vector<String> commandArgumentsBuffer = new Vector<String>();

	public abstract boolean hasMoreData();
	public abstract String nextLine();

	public Command nextCommand() throws Exception {
		if (commandsStringsBuffer.isEmpty()) {
			if (!hasMoreData()) {
				throw new TimeToExitException();
			}
			String line = nextLine();
			String[] commandsSequence = line.split(";");
			Collections.addAll(commandsStringsBuffer, commandsSequence);
		}
		Collections.addAll(commandArgumentsBuffer, commandsStringsBuffer.firstElement().split(" "));
		commandsStringsBuffer.removeElementAt(0);

		// removing white spaces
		while (commandArgumentsBuffer.remove("")) {
		}
		Command theCommand = null;
		try {
			theCommand = Command.createCommand(commandArgumentsBuffer);
		} catch (IllegalArgumentException e) {
			commandsStringsBuffer.clear();
		}
		commandArgumentsBuffer.clear();
		return theCommand;
	}

	public boolean hasUnexecutedCommands() {
		return !commandsStringsBuffer.isEmpty();
	}
}
