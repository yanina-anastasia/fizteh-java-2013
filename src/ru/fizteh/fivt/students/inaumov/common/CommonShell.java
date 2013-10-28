package ru.fizteh.fivt.students.inaumov.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class CommonShell<State> {
	private HashMap<String, Command> commandsMap = new HashMap<String, Command>();
	private State state = null;

	public void setFileMapState(State state) {
		this.state = state;
	}
	
	public void addCommand(Command newCommand) {
		commandsMap.put(newCommand.getName(), newCommand);
	}
	
	public Command getCommand(String commandName) throws UnknownCommandException {
		Command command = commandsMap.get(commandName);

		if (command == null) {
			throw new UnknownCommandException(commandName + ": command not found");
		}
		
		return command;
	}
	
	public static String[][] getCommandsWithArgumentsFromPrgArgs(String commandLine) {
		String[] args = commandLine.split("\\s*;\\s*");
		String[][] commands = new String[args.length][];
		for (int i = 0; i < args.length; ++i) {
			args[i] = args[i].trim();
			commands[i] = args[i].split("\\s+");
		}
		return commands;
	}

	public void executeAllCommands(String[][] commands)
            throws UnknownCommandException, UserInterruptionException, IllegalArgumentException {
		Command nextCommand;

		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].length != 0) {
				nextCommand = getCommand(commands[i][0]);
				if (nextCommand.getArgumentsNumber() != commands[i].length - 1) {
					throw new IllegalArgumentException(nextCommand.getName() + ": expected "
                            + nextCommand.getArgumentsNumber() + " arguments, got " + (commands[i].length - 1));
				}
				nextCommand.execute(commands[i], state);
			}
		}
	}
	
	public void batchMode(String[] args) {
		StringBuilder stringBuilder = new StringBuilder();
		
		for (String nextEntry: args) {
			stringBuilder.append(nextEntry);
			stringBuilder.append(" ");
		}
		
		String[][] commands = getCommandsWithArgumentsFromPrgArgs(stringBuilder.toString());
		
		try {
			executeAllCommands(commands);
		} catch (UnknownCommandException exception) {
			System.err.println(exception.getMessage());
			System.exit(1);
		} catch (IllegalArgumentException exception) {
			System.err.println(exception.getMessage());
			System.exit(1);
		} catch (UserInterruptionException exception) {
			System.exit(0);
		}
		
	}
	
	public void interactiveMode() {

		BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(System.in));
		String[][] commands = null;
		
		while (true) {
            System.out.print(" $ ");
			try {
				commands = getCommandsWithArgumentsFromPrgArgs(inputStreamReader.readLine());
			} catch (IOException exception) {
				System.err.println(exception.getMessage());
			}
			try {
				executeAllCommands(commands);
			} catch (UnknownCommandException exception) {
				System.err.println(exception.getMessage());
			} catch (IllegalArgumentException exception) {
				System.err.println(exception.getMessage());
			} catch (UserInterruptionException exception) {
				System.exit(0);
			}
		}
	}	
}
