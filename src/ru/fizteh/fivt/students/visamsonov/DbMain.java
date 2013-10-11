package ru.fizteh.fivt.students.visamsonov;

import ru.fizteh.fivt.students.visamsonov.shell.Shell;
import java.io.*;

public class DbMain {

	public static void main (String[] args) {
		Shell shell = new Shell();
		try {
			shell.state.database.loadDataToMemory();
		}
		catch (FileNotFoundException e) {
			System.err.println("Database file not found");
			System.exit(1);
		}
		catch (IOException e) {
			System.err.printf("Error: %s\n", e.getMessage());
			System.exit(1);
		}
		if (args.length == 0) {
			shell.interactiveMode();
		}
		else if (!shell.perform(args)) {
			System.exit(1);
		}
		System.exit(0);
	}
}