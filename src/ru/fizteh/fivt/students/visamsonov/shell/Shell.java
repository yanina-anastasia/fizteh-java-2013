package ru.fizteh.fivt.students.visamsonov.shell;

import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class Shell {
	
	private final Map<String, Command> commandList = new HashMap<String, Command>();

	public void addCommand (Command command) {
		commandList.put(command.getName(), command);
	}

	public Shell () {
		addCommand(new CommandExit());
		addCommand(new CommandPut());
		addCommand(new CommandGet());
		addCommand(new CommandRemove());
	}

	public final ShellState state = new ShellState();

	public boolean perform (String[] args) {
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

	public void interactiveMode () {
		Scanner sc = new Scanner(System.in);
		System.out.printf("%s$ ", state.getCurrentDirectory());
		while (sc.hasNextLine()) {
			String[] args = {sc.nextLine()};
			perform(args);
			System.out.printf("%s$ ", state.getCurrentDirectory());
		}
	}

	public static void main (String[] args) {
		Shell shell = new Shell();
		if (args.length == 0) {
			shell.interactiveMode();
		}
		else if (!shell.perform(args)) {
			System.exit(1);
		}
		System.exit(0);
	}
}