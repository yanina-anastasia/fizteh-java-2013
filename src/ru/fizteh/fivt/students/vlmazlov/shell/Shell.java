package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.File;
import java.lang.Thread;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;
import java.util.Scanner;
import java.util.Arrays;
import java.io.OutputStream;

public class Shell {

	private Map<String, Command> supportedCommands;
	private static final String invitation = "$ ";

	private static class WrongCommandException extends Exception {
		public WrongCommandException() { 
			super(); 
		}
		public WrongCommandException(String message) { 
			super(message); 
		}
		public WrongCommandException(String message, Throwable cause) { 
			super(message, cause); 
		}
		public WrongCommandException(Throwable cause) { 
			super(cause); 
		}
	}

	public class ShellState {
		private	String currentDirectory;

		public ShellState(String _currentDirectory) {
			currentDirectory = _currentDirectory;
		}

		public String getCurDir() {
			return currentDirectory;
		}

		//default access modifier used to let commands modify the shell's state, which is desirable for cd, for instance

		void changeCurDir(String newCurDir) {
			currentDirectory = newCurDir;
		}
	}

	public Shell(Command[] commands) {

		Map<String, Command> _supportedCommands = new TreeMap<String, Command>();

		for (Command command : commands) {
			_supportedCommands.put(command.getName(), command);
		}
			
		supportedCommands = Collections.unmodifiableMap(_supportedCommands);
	}
	

	public static void main(String[] args) {

		Command[] commands = {
			new RmCommand(), new CdCommand(), 
			new MvCommand(), new MkdirCommand(), new CpCommand(), 
			new PwdCommand(), new DirCommand(), new ExitCommand()
		};

		Shell shell = new Shell(commands);
		Shell.ShellState state = shell.new ShellState(System.getProperty("user.dir"));

		if (0 != args.length) {
			
			String arg = StringJoiner.join(Arrays.asList(args), " ");

			try {
				shell.executeLine(arg, state);
			} catch (WrongCommandException ex) {
				System.err.println(ex.getMessage());
				System.exit(1);
			} catch (CommandFailException ex) {
				System.err.println("error while processing command: " + ex.getMessage());
				System.exit(2);
			} catch (UserInterruptionException ex) {
				System.exit(0);
			}
		} else {
			shell.interactiveMode(state);
		}

		System.exit(0);
	}

	private String[] parseLine(String commandLine) {
		commandLine = commandLine.trim();
		return commandLine.split("\\s*;\\s*", -1);
	}

	private void executeLine(String commandLine, Shell.ShellState state) 
	throws WrongCommandException, CommandFailException, UserInterruptionException {
		for (String exArg : parseLine(commandLine)) {
			invokeCommand(exArg.split("\\s+"), state);
		}
	}

	private void interactiveMode(Shell.ShellState state) {
		Scanner inputScanner = new Scanner(System.in);
		Scanner stringScanner;

		do {
			//printing invitation
			System.out.print(state.getCurDir() + invitation);

			try {
				executeLine(inputScanner.nextLine(), state);
			} catch (WrongCommandException ex) {
				
				System.err.println(ex.getMessage());
			} catch (CommandFailException ex) {
				
				System.err.println(ex.getMessage());
			} catch (UserInterruptionException ex) {
				
				return; 
			}

		} while (!Thread.currentThread().isInterrupted());
	}

	private void invokeCommand(String[] toExecute, Shell.ShellState state) 
	throws WrongCommandException, CommandFailException, UserInterruptionException {
		//toExecute[0] should be the beginning of the command
		if (0 == toExecute.length) {
			throw new WrongCommandException("Empty command");
		}
		Command invokedCommand = supportedCommands.get(toExecute[0]);
		
		if (null == invokedCommand) {
			throw new WrongCommandException("Unknown command: " + toExecute[0]);
		} else if ((toExecute.length - 1) != invokedCommand.getArgNum()) {
			throw new WrongCommandException("Ivalid number of arguments for " + toExecute[0] + ": " + (toExecute.length - 1));
		}

		invokedCommand.execute(Arrays.copyOfRange(toExecute, 1, toExecute.length), state, System.out);
	}
}
