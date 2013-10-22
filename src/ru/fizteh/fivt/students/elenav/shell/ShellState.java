package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.PrintStream;
import ru.fizteh.fivt.students.elenav.commands.ChangeDirectoryCommand;
import ru.fizteh.fivt.students.elenav.commands.CopyCommand;
import ru.fizteh.fivt.students.elenav.commands.ExitShellCommand;
import ru.fizteh.fivt.students.elenav.commands.MakeDirectoryCommand;
import ru.fizteh.fivt.students.elenav.commands.MoveCommand;
import ru.fizteh.fivt.students.elenav.commands.PrintDirectoryCommand;
import ru.fizteh.fivt.students.elenav.commands.PrintWorkingDirectoryCommand;
import ru.fizteh.fivt.students.elenav.commands.RmCommand;

public class ShellState extends FilesystemState {
	
	ShellState(String n, File pd, PrintStream s) {
		super(n, pd, s);
	}
	
	protected void init() {
		commands.add(new ChangeDirectoryCommand(this));
		commands.add(new MakeDirectoryCommand(this));
		commands.add(new PrintWorkingDirectoryCommand(this));
		commands.add(new RmCommand(this));
		commands.add(new CopyCommand(this));
		commands.add(new MoveCommand(this));
		commands.add(new PrintDirectoryCommand(this));
		commands.add(new ExitShellCommand(this));
	}
		
}
