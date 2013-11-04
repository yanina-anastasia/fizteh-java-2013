package ru.fizteh.fivt.students.elenav.shell;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;


public class Shell {
	
	public static ShellState createShellState(String nm, File f, PrintStream ps) {
		ShellState shell = new ShellState(nm, f, ps);
		shell.init();
		return shell;
	}
	
	public static void main(String[] args) throws IOException {
		ShellState shell = createShellState("my new state", new File("."), System.out);
		shell.run(args);
	}
}