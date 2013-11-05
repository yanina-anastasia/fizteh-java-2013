package ru.fizteh.fivt.students.vlmazlov.filemap;

import ru.fizteh.fivt.students.vlmazlov.shell.Shell;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import ru.fizteh.fivt.students.vlmazlov.shell.WrongCommandException;
import ru.fizteh.fivt.students.vlmazlov.shell.UserInterruptionException;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityCheckFailedException;
import ru.fizteh.fivt.students.vlmazlov.shell.Command;
import ru.fizteh.fivt.students.vlmazlov.shell.ExitCommand;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.DataBaseReader;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.DataBaseWriter;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
	public static void main(String[] args) {
		FileMap fileMap = new FileMap(); 

		 if (System.getProperty("fizteh.db.dir") == null) {
			System.err.println("Directory not specified");
			System.exit(1);
		 }

		try {
			DataBaseReader.readFileMap(new File(System.getProperty("fizteh.db.dir")), 
				new File(System.getProperty("fizteh.db.dir"), "db.dat"), fileMap);
		} catch (IOException ex) {
			System.err.println("Unable to retrieve entries from file" + ex.getMessage());
			System.exit(2);
		} catch (ValidityCheckFailedException ex) {
			System.err.println("Validity check failed: " + ex.getMessage());
			System.exit(3);
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

		try {
			DataBaseWriter.writeFileMap(System.getProperty("fizteh.db.dir"), "db.dat", fileMap);
		} catch (IOException ex) {
			System.err.println("Unable to store entries in the file: " + ex.getMessage());
			System.exit(6);
		}

		System.exit(0);
	}
}