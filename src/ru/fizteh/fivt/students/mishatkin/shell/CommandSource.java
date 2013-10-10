package ru.fizteh.fivt.students.mishatkin.shell;

import java.util.*;

/**
 * CommandSource.java
 * Created by Vladimir Mishatkin on 9/23/13
 */
public abstract class CommandSource {
	private static  final Map<CommandType, Integer> INPUT_ARGUMENTS_COUNTS;
	static {
		Map<CommandType, Integer> aMap = new HashMap<CommandType, Integer>();
		aMap.put(CommandType.CD, 1);
		aMap.put(CommandType.MKDIR, 1);
		aMap.put(CommandType.PWD, 0);
		aMap.put(CommandType.RM, 1);
		aMap.put(CommandType.CP, 2);
		aMap.put(CommandType.MV, 2);
		aMap.put(CommandType.DIR, 0);
		aMap.put(CommandType.EXIT, 0);
		INPUT_ARGUMENTS_COUNTS = Collections.unmodifiableMap(aMap);
	}
	private ArrayList<String> commandsStringsBuffer = new ArrayList<String>();
	private ArrayList<String> commandArgumentsBuffer = new ArrayList<String>();

	public void clearBuffers() {
		commandArgumentsBuffer.clear();
		commandsStringsBuffer.clear();
	}
	public Command nextCommandForReceiver(CommandReceiver receiver) throws ShellException {
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
			theCommand = createCommand(receiver);
		} catch (IllegalArgumentException e) {
			commandsStringsBuffer.clear();
		}
		commandArgumentsBuffer.clear();
		return theCommand;
	}

	private Command createCommand(CommandReceiver receiver) throws ShellException {
		if (commandArgumentsBuffer.isEmpty()) {
			return null;
		}
		Command retValue =  null;
		String commandName = commandArgumentsBuffer.get(0);
		CommandType theType;
		try {
			theType = CommandType.valueOf(commandName.toUpperCase());
		} catch (IllegalArgumentException e) {
			commandArgumentsBuffer.removeAll(commandArgumentsBuffer);
			String enumName = "CommandType.";
			String type = e.getMessage().substring( e.getMessage().indexOf(enumName) + enumName.length()).toLowerCase();
			throw new ShellException("Invalid command: \'" + type + "\'.");
		}
		switch (theType) {
			case CD:
				retValue = new ChangeDirectoryCommand(receiver);
				break;
			case MKDIR:
				retValue = new MakeDirectoryCommand(receiver);
				break;
			case PWD:
				retValue = new PrintWorkingDirectoryCommand(receiver);
				break;
			case RM:
				retValue = new RemoveCommand(receiver);
				break;
			case CP:
				retValue = new CopyCommand(receiver);
				break;
			case MV:
				retValue = new MoveCommand(receiver);
				break;
			case DIR:
				retValue = new DirectoryCommand(receiver);
				break;
			case EXIT:
				retValue = new ExitCommand(receiver);
				break;
		}
		if (retValue != null) {
			readArgs(retValue);
		}
		return retValue;
	}

	private void readArgs(Command command) throws ShellArgumentsMismatchException {
		if (INPUT_ARGUMENTS_COUNTS.get(command.getType()) != commandArgumentsBuffer.size() - 1) {
			throw new ShellArgumentsMismatchException("Invalid number of arguments for command \'"
					+ command.type.toString().toLowerCase() + "\'.");
		}
		for (int argumentIndex = 0; argumentIndex < INPUT_ARGUMENTS_COUNTS.get(command.getType()); ++argumentIndex) {
			command.args[argumentIndex] = commandArgumentsBuffer.get(argumentIndex + 1);
		}
	}

	public boolean hasUnexecutedCommands() {
		return !commandsStringsBuffer.isEmpty();
	}

	public abstract boolean hasMoreData();
	public abstract String nextLine();
}
