package ru.fizteh.fivt.students.inaumov.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Shell {
	private final String invitationString = "$ ";
	private final Map<String, Command> commandsMap = new HashMap<String, Command>();
    private final OutputStream outputStream;

    public ShellState shellState;

	public class ShellState {
		public FileCommander fileCommander;
		
		public ShellState() {
			fileCommander = new FileCommander(outputStream);
		}
	}
	
	public Shell(OutputStream outputStream) {
		this.outputStream = outputStream;
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
                if (nextCommand.getArgumentsNumber() != commands[i].length - 1) {
                    throw new CommandExecutionFailException(nextCommand.getName() + ": expected " + nextCommand.getArgumentsNumber() + " arguments, got " + (commands[i].length - 1) + " arguments");
                }
				nextCommand.executeCommand(commands[i], shellState);
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
		} catch (CommandExecutionFailException exception) {
			System.err.println(exception.getMessage());
			System.exit(1);
		} catch (UserInterruptionException exception) {
			System.exit(0);
		}
	}
	public void interactiveMode() throws IOException {
		BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(System.in)); 
		
		while (true) {
			System.out.print(shellState.fileCommander.getCurrentDirectory() + invitationString);
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
		Shell shell = new Shell(System.out);
		shell.addCommand(new CdCommand());
		shell.addCommand(new MkdirCommand());
		shell.addCommand(new PwdCommand());
		shell.addCommand(new RmCommand());
		shell.addCommand(new CpCommand());
		shell.addCommand(new MvCommand());
		shell.addCommand(new DirCommand());
		shell.addCommand(new ExitCommand());
		
		if (args.length == 0) {
			try {
				shell.interactiveMode();
			} catch (IOException exception) {
				System.err.println(exception.getMessage());
				System.exit(1);
			}
		} else {
			shell.batchMode(args);
		}
	}
}