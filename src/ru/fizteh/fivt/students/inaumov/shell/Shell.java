package ru.fizteh.fivt.students.inaumov.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Shell {
	public static final String INVITE = "$ ";
	private Map<String, Command> commandsMap;
	ShellState shellState;
	public class ShellState {
		public FileCommander fileCommander;
		
		ShellState() {
			fileCommander = new FileCommander();
		}
	}
	
	public Shell() {
		commandsMap = new HashMap<String, Command>();
		shellState = new ShellState();
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
	public void executeAllCommands(String[][] commands) throws UnknownCommandException, UserInterruptionException, CommandExecutionFailException {
		Command nextCommand;
		
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].length != 0) {
				nextCommand = getCommand(commands[i][0]);
				nextCommand.executeCommand(commands[i], shellState);
			}
		}
	}
	public void packetMode(String[] args) {
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
		} catch (CommandExecutionFailException exception) {
			System.err.println(exception.getMessage());
		} catch (UserInterruptionException exception) {
			System.exit(0);
		}
	}
	public void interactiveMode() throws IOException {
		BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(System.in)); 
		
		while (true) {
			System.out.print(shellState.fileCommander.getCurrentDirectory() + INVITE);
			String[][] commands = getCommandsWithArgumentsFromPrgArgs(inputStreamReader.readLine());
			try {
				executeAllCommands(commands);
			} catch (UnknownCommandException exception) {
				System.err.println(exception.getMessage());
			} catch (CommandExecutionFailException exception) {
				System.err.println(exception.getMessage());
			} catch (UserInterruptionException exception) {
				System.exit(0);
			}
		}
	}
	
	public static void main(String[] args) {
		Shell shell = new Shell();
		shell.addCommand(new CDcommand());
		shell.addCommand(new MKDIRcommand());
		shell.addCommand(new PWDcommand());
		shell.addCommand(new RMcommand());
		shell.addCommand(new CPcommand());
		shell.addCommand(new MVcommand());
		shell.addCommand(new DIRcommand());
		shell.addCommand(new EXITcommand());
		
		if (args.length == 0) {
			try {
				shell.interactiveMode();
			} catch (IOException exception) {
				System.err.println(exception.getMessage());
			}
		} else {
			shell.packetMode(args);
		}
	}
}