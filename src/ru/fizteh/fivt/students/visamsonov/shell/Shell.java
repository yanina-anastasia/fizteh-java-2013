package ru.fizteh.fivt.students.visamsonov.shell;

import java.util.Scanner;
import java.util.TreeMap;

public class Shell {
	
	private static final TreeMap<String, Command> commandList = new TreeMap<String, Command>();

	static {
		Command command;
		command = new CommandExit();
		commandList.put(command.getName(), command);
		command = new CommandPut();
		commandList.put(command.getName(), command);
		command = new CommandGet();
		commandList.put(command.getName(), command);
		command = new CommandRemove();
		commandList.put(command.getName(), command);
	}

	public static ShellState state = new ShellState();

	public static boolean perform (String[] args) {
		ArgumentParser parser = new ArgumentParser(args);
		for (RawCommand command = parser.nextCommand(); command != null; command = parser.nextCommand()) {
			Command availableCommand = commandList.get(command.name);
			if (availableCommand == null) {
				System.err.printf("%s: command not found\n", command.name);
				return false;
			}
			if (!availableCommand.evaluate(state, command.args)) {
				return false;
			}
		}
		return true;
	}

	public static void interactiveMode () {
		Scanner sc = new Scanner(System.in);
		System.out.printf("%s$ ", state.getCurrentDirectory());
		while (sc.hasNextLine()) {
			String[] args = {sc.nextLine()};
			perform(args);
			System.out.printf("%s$ ", state.getCurrentDirectory());
		}
	}

	public static void main (String[] args) {
		if (args.length == 0) {
			interactiveMode();
		}
		else if (!perform(args)) {
			System.exit(1);
		}
		System.exit(0);
	}
}