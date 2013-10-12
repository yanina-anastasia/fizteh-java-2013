package ru.fizteh.fivt.students.visamsonov.shell;

import ru.fizteh.fivt.students.visamsonov.storage.*;
import java.io.*;
import java.util.TreeMap;

public class ShellState {

	public Table database;

	public ShellState () throws FileNotFoundException, IOException {
		database = new FileStorage(System.getProperty("fizteh.db.dir"), "db.dat");
	}
}