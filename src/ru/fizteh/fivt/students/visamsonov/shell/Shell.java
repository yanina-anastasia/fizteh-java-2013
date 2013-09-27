package ru.fizteh.fivt.students.visamsonov.shell;

import java.util.Scanner;

public class Shell {
	
	private static final Command[] commandList = {new CommandPwd(), new CommandExit(), new CommandDir(),
	                                              new CommandMkdir(), new CommandCd(), new CommandRm(),
	                                              new CommandMv(), new CommandCp()};

	public static boolean parse (String[] args) {
		ArgumentParser parser = new ArgumentParser(args);
		for (RawCommand command = parser.nextCommand(); command != null; command = parser.nextCommand()) {
			int i;
			for (i = 0; i < commandList.length; i++) {
				if (commandList[i].getName().equals(command.name)) {
					commandList[i].evaluate(command.args);
					break;
				}
			}
			if (i == commandList.length) {
				System.err.printf("%s: command not found\n", command.name);
				return false;
			}
		}
		return true;
	}

	public static void interactiveMode () {
		Scanner sc = new Scanner(System.in);
		System.out.printf("%s$ ", Utils.getCurrentDirectory());
		while (sc.hasNextLine()) {
			String[] args = {sc.nextLine()};
			parse(args);
			System.out.printf("%s$ ", Utils.getCurrentDirectory());
		}
	}

	public static void main (String[] args) {
		if (args.length == 0) {
			interactiveMode();
		}
		else if (!parse(args)) {
			System.exit(1);
		}
		System.exit(0);
	}
}