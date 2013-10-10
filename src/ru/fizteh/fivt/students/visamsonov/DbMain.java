package ru.fizteh.fivt.students.visamsonov;

import ru.fizteh.fivt.students.visamsonov.shell.Shell;
import java.io.*;

public class DbMain {

	public static void main (String[] args) {
		try {
			Shell.state.database.loadDataToMemory();
		}
		catch (FileNotFoundException e) {
			System.err.println("Database file not found");
			System.exit(1);
		}
		catch (IOException e) {
			System.err.printf("Error: %s\n", e.getMessage());
			System.exit(1);
		}
		Shell.main(args);
	}
}