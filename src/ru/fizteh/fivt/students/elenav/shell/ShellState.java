package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import ru.fizteh.fivt.students.elenav.commands.ChangeDirectoryCommand;
import ru.fizteh.fivt.students.elenav.commands.MakeDirectoryCommand;
import ru.fizteh.fivt.students.elenav.commands.RmCommand;
import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class ShellState extends FilesystemState implements ShellFace {
	
	public ShellState(String n, File wd, PrintStream s) {
		super(n, wd, s);
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
