package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import ru.fizteh.fivt.storage.structured.Storeable;
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
		c.execute(args);
	}

	public void makeDirectory(String name) throws IOException {
		MakeDirectoryCommand c = new MakeDirectoryCommand(this);
		String[] args = {"mkdir", name};
		c.execute(args);
	}

	public void rm(String name) throws IOException {
		RmCommand c = new RmCommand(this);
		String[] args = {"rm", name};
		c.execute(args);
	}

	@Override
	public int commit() {
		System.err.print("Command can't be executed");
		return 0;
	}

	@Override
	public Object get(String string) {
		System.err.print("Command can't be executed");
		return null;
	}

	@Override
	public String put(String string, String string2) {
		System.err.print("Command can't be executed");
		return null;
	}

	@Override
	public String remove(String string) {
		System.err.print("Command can't be executed");
		return null;
	}

	@Override
	public int rollback() {
		System.err.print("Command can't be executed");
		return 0;
	}

	@Override
	public int size() {
		System.err.print("Command can't be executed");
		return 0;
	}

	@Override
	public int getNumberOfChanges() {
		System.err.print("Command can't be executed");
		return 0;
	}

	@Override
	public void read() throws IOException {
		System.err.print("Command can't be executed");
	}

	@Override
	public Storeable put(String string, Storeable string2) {
		System.err.println("Command can't be executed");
		return null;
	}

}
