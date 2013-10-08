package ru.fizteh.fivt.students.mishatkin.shell;

import java.util.ArrayList;
import java.util.Collections;

/**
 * CommandSource.java
 * Created by Vladimir Mishatkin on 9/23/13
 */
public abstract class CommandSource {
	private ArrayList<String> commandsStringsBuffer = new ArrayList<String>();
	private ArrayList<String> commandArgumentsBuffer = new ArrayList<String>();

	public abstract boolean hasMoreData();
	public abstract String nextLine();

	public Command nextCommand() throws ShellException {
		if (commandsStringsBuffer.isEmpty()) {
			if (!hasMoreData()) {
				throw new TimeToExitException();
			}
			String line = nextLine();
			String[] commandsSequence = line.split(";");
			Collections.addAll(commandsStringsBuffer, commandsSequence);
		}
		Collections.addAll(commandArgumentsBuffer, commandsStringsBuffer.get(0).split(" "));
		commandsStringsBuffer.remove(0);

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
