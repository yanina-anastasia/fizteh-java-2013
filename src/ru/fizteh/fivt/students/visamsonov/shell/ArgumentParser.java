package ru.fizteh.fivt.students.visamsonov.shell;

public class ArgumentParser {
	
	private String[] commands = {};
	private int iterator = 0;

	public ArgumentParser (String[] args) {
		String allCommands = Utils.stringArrayJoin(args, " ");
		commands = allCommands.split("[\n\t ]*;[\n\t ]*");
	}

	public RawCommand nextCommand () {
		while (iterator < commands.length) {
			iterator++;
			if (!commands[iterator - 1].equals("")) {
				String command = commands[iterator - 1].trim();
				int firstSpace = command.indexOf(" ");
				if (firstSpace == -1) {
					firstSpace = command.length();
				}
				return new RawCommand(command.substring(0, firstSpace), command.substring(firstSpace, command.length()).trim());
			}
		}
		return null;
	}
}