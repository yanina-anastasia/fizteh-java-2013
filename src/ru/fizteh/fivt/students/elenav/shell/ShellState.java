package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.commands.ChangeDirectoryCommand;
import ru.fizteh.fivt.students.elenav.commands.CopyCommand;
import ru.fizteh.fivt.students.elenav.commands.ExitCommand;
import ru.fizteh.fivt.students.elenav.commands.MakeDirectoryCommand;
import ru.fizteh.fivt.students.elenav.commands.MoveCommand;
import ru.fizteh.fivt.students.elenav.commands.PrintDirectoryCommand;
import ru.fizteh.fivt.students.elenav.commands.PrintWorkingDirectoryCommand;
import ru.fizteh.fivt.students.elenav.commands.RmCommand;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class ShellState extends FilesystemState implements ShellFace {
	
	ShellState(String n, File wd, PrintStream s) {
		super(n, wd, s);
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

	public void changeDirectory(String name) throws IOException {
		ChangeDirectoryCommand c = new ChangeDirectoryCommand(this);
		String[] args = {"cd", name};
		c.execute(args, getStream());
	}

	public void makeDirectory(String name) throws IOException {
		MakeDirectoryCommand c = new MakeDirectoryCommand(this);
		String[] args = {"mkdir", name};
		c.execute(args, getStream());
	}

	public void rm(String name) throws IOException {
		RmCommand c = new RmCommand(this);
		String[] args = {"rm", name};
		c.execute(args, getStream());
	}

}
