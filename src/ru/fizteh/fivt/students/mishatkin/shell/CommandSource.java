package ru.fizteh.fivt.students.mishatkin.shell;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

/**
 * CommandSource.java
 * Created by Vladimir Mishatkin on 9/23/13
 */
public abstract class CommandSource {
	private static final String COMMANDS_ROOT_PACKAGE_NAME = "ru.fizteh.fivt.students.mishatkin";
	private Collection<Class> availableClasses = null;
	private String commandClassesRootPackageName = COMMANDS_ROOT_PACKAGE_NAME;

	private ArrayList<String> commandsStringsBuffer = new ArrayList<>();
	private ArrayList<String> commandArgumentsBuffer = new ArrayList<>();

	public void clearBuffers() {
		commandArgumentsBuffer.clear();
		commandsStringsBuffer.clear();
	}

	public Command nextCommand(ShellReceiver receiver) throws ShellException {
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

	private Command createCommand(ShellReceiver receiver) throws ShellException {
		if (commandArgumentsBuffer.isEmpty()) {
			return null;
		}
		ShellCommand retValue =  null;
		StringBuffer commandNameBuffer = new StringBuffer(commandArgumentsBuffer.get(0).toLowerCase());
		commandNameBuffer.setCharAt(0, (char) (commandNameBuffer.charAt(0) + 'A' - 'a'));
		String commandName = new String(commandNameBuffer);
		Class commandClass = null;
		Collection<Class> availableClasses = getAvailableClasses();
		for (Class consideredClass : availableClasses) {
			if (consideredClass.getName().contains(commandName + "Command")) {
				commandClass = consideredClass;
				break;
			}
		}
		if (commandClass == null) {
			throw new ShellException(commandArgumentsBuffer.get(0) + ": invalid command.");
		}
		Class preferredReceiverClass = receiver.getClass();
		Constructor<?> constructorWithReceiverParameter = null;
		do {
			Class tmpCommandClass = commandClass;
			do {
				try {
					constructorWithReceiverParameter = tmpCommandClass.getConstructor(preferredReceiverClass);
				} catch (NoSuchMethodException wellThenJustKeepLookingUp) {
				}
				tmpCommandClass = tmpCommandClass.getSuperclass();
			} while (tmpCommandClass != Object.class && constructorWithReceiverParameter == null);
			preferredReceiverClass = preferredReceiverClass.getSuperclass();
		} while (preferredReceiverClass != Object.class && constructorWithReceiverParameter == null);
		//	first available constructor up the dispatch table tree with single Receiver parameter will be found
		//	due to linearity of the search procedure

		try {
			if (constructorWithReceiverParameter == null) {
				throw new ShellException("No suitable public constructor for command " + commandClass.getName() + ".");
			}
			retValue = (ShellCommand) constructorWithReceiverParameter.newInstance(receiver);
		} catch (InstantiationException e) {
			throw new ShellException("No suitable public constructor for command " + commandClass.getName() + ".");
		} catch (IllegalAccessException | InvocationTargetException e) {
		}
		if (retValue != null) {
			readArgs(retValue);
		}
		return retValue;
	}

	private void readArgs(Command command) throws ShellException {
		if (command.getArgumentsCount() != commandArgumentsBuffer.size() - 1) {
			throw new ShellException("Invalid number of arguments for command \'" + command.getName() + "\'.");
		}
		String[] args = new String[command.getArgumentsCount()];
		for (int argumentIndex = 0; argumentIndex < command.getArgumentsCount(); ++argumentIndex) {
			args[argumentIndex] = commandArgumentsBuffer.get(argumentIndex + 1);
		}
		command.setArguments(args);
	}

// OH NOOOO
	public void setCommandClassesRootPackageName(String commandClassesPackageName) {
		this.commandClassesRootPackageName = commandClassesPackageName;
		availableClasses = null;
	}

	public static String getPackageNameForClassLocation(Class targetClass) {
		return targetClass.getCanonicalName().substring(0, targetClass.getCanonicalName().lastIndexOf(".") + 1);
	}

	private Collection<Class> getAvailableClasses() {
		if (availableClasses == null) {
			availableClasses = findAvailableClasses();
		}
		return availableClasses;
	}

	private Collection<Class> findAvailableClasses() {
		String packageName = commandClassesRootPackageName;
		List<Class> commands = new ArrayList<>();
		URL root = Thread.currentThread().getContextClassLoader().getResource(packageName.replace(".", "/"));
		assert root != null;
		commands.addAll(lookup(packageName, new File(root.getFile())));
		return commands;
	}

	private static Collection<Class> lookup(String packageName, File root) {
		// Filter .class files.
		File[] files = root.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".class");
			}
		});
		List<Class> commands = new ArrayList<>();
		if (files == null) {
			return commands;
		}
		for (File file : files) {
			String className = file.getName().replaceAll(".class$", "");
			Class<?> cls = null;
			try {
				cls = Class.forName(packageName + "." + className);
			} catch (ClassNotFoundException e) {
			}
			commands.add(cls);
		}
		File[] subPackages = root.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String s) {
				return file.isDirectory();
			}
		});
		for (File subPackage : subPackages) {
			String subName =  packageName + "." + subPackage.getName();
			commands.addAll(lookup(subName, new File(root, subPackage.getName())));
		}
		return commands;
	}



	public boolean hasUnexecutedCommands() {
		return !commandsStringsBuffer.isEmpty();
	}

	public abstract boolean hasMoreData();
	public abstract String nextLine();
}
