package ru.fizteh.fivt.students.msandrikova.filemap;

import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.ExitCommand;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;

public class Launcher {
	private static Command[] commands = new Command[] {
		new PutCommand(),
		new GetCommand(),
		new RemoveCommand(),
		new ExitCommand()
	};
	

	public static void main(String[] args) {
		String currentDirectory = "."; //System.getProperty("fizteh.db.dir");
		Shell myShell = new Shell(commands, currentDirectory);
		myShell.execute(args);
	}

}