package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.msandrikova.filemap.GetCommand;
import ru.fizteh.fivt.students.msandrikova.filemap.PutCommand;
import ru.fizteh.fivt.students.msandrikova.filemap.RemoveCommand;
import ru.fizteh.fivt.students.msandrikova.shell.Command;
import ru.fizteh.fivt.students.msandrikova.shell.ExitCommand;
import ru.fizteh.fivt.students.msandrikova.shell.Shell;
import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class Launcher {
	private static Command[] commands = new Command[] {
		new PutCommand(),
		new GetCommand(),
		new RemoveCommand(),
		new CreateCommand(),
		new DropCommand(),
		new UseCommand(),
		new ExitCommand()
	};

	public static void main(String[] args) {
		String currentDirectory = System.getProperty("fizteh.db.dir");
		if(currentDirectory == null) {
			Utils.generateAnError("Incorrect work getProperty().", "Launcher", false);
		}
		State myState = new State();
		MyTableProviderFactory myTableProviderFactory = new MyTableProviderFactory();
		TableProvider currentTableProvider = null;
		try {
			currentTableProvider = myTableProviderFactory.create(currentDirectory);
		} catch (IllegalArgumentException e) {
			Utils.generateAnError(e.getMessage(), "Launcher", false);
		}
		myState.setTableProvider(currentTableProvider);
		myState.setIsMultiFileHashMap(true);
		Shell myShell = new Shell(commands, currentDirectory);
		myShell.setState(myState);
		myShell.execute(args);

	}

}
