package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import ru.fizteh.fivt.students.elenav.commands.AbstractCommand;
import ru.fizteh.fivt.students.elenav.commands.ChangeDirectoryCommand;
import ru.fizteh.fivt.students.elenav.commands.Command;
import ru.fizteh.fivt.students.elenav.commands.CopyCommand;
import ru.fizteh.fivt.students.elenav.commands.ExitCommand;
import ru.fizteh.fivt.students.elenav.commands.MakeDirectoryCommand;
import ru.fizteh.fivt.students.elenav.commands.MoveCommand;
import ru.fizteh.fivt.students.elenav.commands.PrintDirectoryCommand;
import ru.fizteh.fivt.students.elenav.commands.PrintWorkingDirectoryCommand;
import ru.fizteh.fivt.students.elenav.commands.RmCommand;

public class ShellState extends State {
	
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
		commands.add(new ExitCommand(this));
	}
		
}
