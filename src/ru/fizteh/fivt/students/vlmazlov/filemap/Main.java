package ru.fizteh.fivt.students.vlmazlov.filemap;

import ru.fizteh.fivt.students.vlmazlov.shell.Shell;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import ru.fizteh.fivt.students.vlmazlov.shell.WrongCommandException;
import ru.fizteh.fivt.students.vlmazlov.shell.UserInterruptionException;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityCheckFailedException;
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
		} catch (StorageNotFoundException ex) {
			isStored = false;
		} catch (FileNotFoundException ex) {
			System.err.println("Unable to retrieve entries from file");
			System.exit(1);
		} catch (ValidityCheckFailedException ex) {
			System.err.println("Validity check failed: " + ex.getMessage());
			System.exit(2);
		}
		
		if (isStored) {
			try {
				reader.read();
			} catch (IOException ex) {
				System.err.println("Unable to retrieve entries from file");
				System.exit(3);
			} catch (ValidityCheckFailedException ex) {
				System.err.println("Validity check failed: " + ex.getMessage());
				System.exit(8);
			}
		}

		Command[] commands = {
			new PutCommand(), new GetCommand(), 
			new RemoveCommand(), new ExitCommand()
		};

		Shell<FileMap> shell = new Shell<FileMap>(commands, fileMap);

		try {
			shell.process(args);
		} catch (WrongCommandException ex) {
			System.err.println(ex.getMessage());
			System.exit(4);
		} catch (CommandFailException ex) {
			System.err.println("error while processing command: " + ex.getMessage());
			System.exit(5);
		} catch (UserInterruptionException ex) {
		}

		DataBaseWriter writer = null;

		try {
			writer = new DataBaseWriter(System.getProperty("fizteh.db.dir"), "db.dat", fileMap);
		} catch (FileNotFoundException ex) {
			System.err.println("Unable to store entries in the file");
			System.exit(6);
		}

		try {
			writer.write();
		} catch (IOException ex) {
			System.err.println("Unable to store entries in the file: " + ex.getMessage());
			System.exit(7);
		}

		System.exit(0);
	}
}