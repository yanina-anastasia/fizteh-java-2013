package ru.fizteh.fivt.students.vlmazlov.filemap;

import ru.fizteh.fivt.students.vlmazlov.shell.Shell;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import ru.fizteh.fivt.students.vlmazlov.shell.WrongCommandException;
import ru.fizteh.fivt.students.vlmazlov.shell.UserInterruptionException;
import ru.fizteh.fivt.students.vlmazlov.shell.Command;
import ru.fizteh.fivt.students.vlmazlov.shell.ExitCommand;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.FileNotFoundException;

public class Main {
	public static void main(String[] args) {
		FileMap fileMap = new FileMap(); 
		boolean isStored = true;
		DataBaseReader reader = null;

		try {
			reader = new DataBaseReader(System.getProperty("fizteh.db.dir"), "db.dat", fileMap);
		} catch (FileNotFoundException ex) {
			System.err.println("Unable to retrieve entries from file: " + ex.getMessage());
			System.exit(1);
		} catch (IOException ex) {
			isStored = false;
		}

		if (isStored) {
			try {
				reader.retrieveFromFile();
			} catch (IOException ex) {
				System.err.println("Unable to retrieve entries from file: " + ex.getMessage());
				System.exit(2);
			}
		}

		Command[] commands = {
			new PutCommand(fileMap), new GetCommand(fileMap), 
			new RemoveCommand(fileMap), new ExitCommand()
		};

		Shell shell = new Shell(commands);
		Shell.ShellState state = shell.new ShellState(System.getProperty("fizteh.db.dir"));

		try {
			shell.process(args, state);
		} catch (WrongCommandException ex) {
			System.err.println(ex.getMessage());
			System.exit(3);
		} catch (CommandFailException ex) {
			System.err.println("error while processing command: " + ex.getMessage());
			System.exit(4);
		} catch (UserInterruptionException ex) {
		}

		DataBaseWriter writer = null;

		try {
			writer = new DataBaseWriter(System.getProperty("fizteh.db.dir"), "db.dat", fileMap);
		} catch (FileNotFoundException ex) {
			System.err.println("Unable to store entries in the file: " + ex.getMessage());
			System.exit(5);
		}

		try {
			writer.storeInFile();
		} catch (IOException ex) {
			System.err.println("Unable to store entries in the file: " + ex.getMessage());
			System.exit(6);
		}

		System.exit(0);
	}
}