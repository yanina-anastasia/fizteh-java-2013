package ru.fizteh.fivt.students.mishatkin.filemap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Vladimir Mishatkin on 10/15/13
 */
public abstract class CommandSource {
	private static final String PACKAGE_NAME = CommandSource.class.getCanonicalName().substring(0,
	                                   CommandSource.class.getCanonicalName().indexOf(Command.class.getSimpleName()));
	private ArrayList<String> commandsStringsBuffer = new ArrayList<>();
	private ArrayList<String> commandArgumentsBuffer = new ArrayList<>();

	public void clearBuffers() {
		commandArgumentsBuffer.clear();
		commandsStringsBuffer.clear();
	}

	public Command nextCommand(FileMapReceiver receiver) throws FileMapException {
		if (commandsStringsBuffer.isEmpty()) {
			if (!hasMoreData()) {
				throw new TimeToExitException();
			}
			String line = nextLine();
			String[] commandsSequence = line.split(";");
			Collections.addAll(commandsStringsBuffer, commandsSequence);
		}
		Collections.addAll(commandArgumentsBuffer, commandsStringsBuffer.get(0).trim().split("\\s+"));
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

	private Command createCommand(FileMapReceiver receiver) throws FileMapException {
		if (commandArgumentsBuffer.isEmpty()) {
			return null;
		}
		FileMapCommand retValue =  null;
		StringBuffer commandNameBuffer = new StringBuffer(commandArgumentsBuffer.get(0).toLowerCase());
		commandNameBuffer.setCharAt(0, (char) (commandNameBuffer.charAt(0) + 'A' - 'a'));
		String commandName = new String(commandNameBuffer);
		try {
			Class commandClass = Class.forName(PACKAGE_NAME + commandName + "Command");
			ArrayList<Constructor<?>> availableConstructors = new ArrayList<>();
			Class tmpCommandClass = commandClass;
			do {
				try {
					availableConstructors.add(tmpCommandClass.getConstructor(FileMapReceiver.class));
				} catch (NoSuchMethodException e) {
				}
				tmpCommandClass = tmpCommandClass.getSuperclass();
			} while (tmpCommandClass != Object.class);
			Constructor<?> constructorWithReceiverParameter = null;
			for (Constructor<?> constructor : availableConstructors) {
				if (constructor.getParameterTypes().length == 1 &&
				    constructor.getParameterTypes()[0].equals(FileMapReceiver.class))
				{
					constructorWithReceiverParameter = constructor;
					//	first available constructor up the dispatch table tree with single Receiver parameter will be found
					//	due to linearity of ArrayList and sequential adding all constructors upp the tree
					break;
				}
			}
			try {
				assert constructorWithReceiverParameter != null;
				retValue = (FileMapCommand) constructorWithReceiverParameter.newInstance(receiver);
			} catch (InstantiationException e) {
				throw new FileMapException("No declared ovverriden constructor in subclass! " + commandClass.getName() + ". GO ADD IT NOW!!!1");
			} catch (IllegalAccessException | InvocationTargetException e) {
			}
		} catch (ClassNotFoundException e) {
			String className = e.getMessage().toLowerCase();
			String invalidCommandName = className.substring(className.lastIndexOf(".") + 1, className.length() - "Command".length());
			throw new FileMapException(invalidCommandName + ": invalid command.");
		}
		if (retValue != null) {
			readArgs(retValue);
		}
		return retValue;
	}

	private void readArgs(Command command) throws FileMapException {
		if (command.getArgumentsCount() != commandArgumentsBuffer.size() - 1) {
			throw new FileMapException("Invalid number of arguments for command \'" + command.getName() + "\'.");
		}
		String[] args = new String[command.getArgumentsCount()];
		for (int argumentIndex = 0; argumentIndex < command.getArgumentsCount(); ++argumentIndex) {
			args[argumentIndex] = commandArgumentsBuffer.get(argumentIndex + 1);
		}
		command.setArguments(args);
	}

	public boolean hasUnexecutedCommands() {
		return !commandsStringsBuffer.isEmpty();
	}

	public abstract boolean hasMoreData();
	public abstract String nextLine();
}
