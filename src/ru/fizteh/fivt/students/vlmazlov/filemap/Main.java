package ru.fizteh.fivt.students.vlmazlov.filemap;

import java.text.ParseException;
import ru.fizteh.fivt.students.vlmazlov.shell.Shell;
import ru.fizteh.fivt.students.vlmazlov.shell.FileUtils;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import ru.fizteh.fivt.students.vlmazlov.shell.WrongCommandException;
import ru.fizteh.fivt.students.vlmazlov.shell.UserInterruptionException;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityCheckFailedException;
import ru.fizteh.fivt.students.vlmazlov.shell.Command;
import ru.fizteh.fivt.students.vlmazlov.shell.ExitCommand;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.GetCommand;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.PutCommand;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.RemoveCommand;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.DataBaseState;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.StringTableProvider;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
	public static void main(String[] args) { 
		DataBaseState<String, StringTable> state = null;
		StringTable table = null;
		File tempDir = null;
		try {
			table = new StringTable("table", true);
			tempDir = FileUtils.createTempDir("providerstate", null);
			if (tempDir == null) {
				System.err.println("Unable to create a temporary directory");
				System.exit(1);
			}
			state = new DataBaseState(new StringTableProvider(tempDir.getPath(), true));
			state.setActiveTable(table);
		} catch (IllegalArgumentException ex) {
			System.err.println(ex.getMessage());
			System.exit(1);
		} catch (ValidityCheckFailedException ex) {
			System.err.println(ex.getMessage());
			System.exit(1);
		}

		try {
			table.read(System.getProperty("fizteh.db.dir"), "db.dat");
		} catch (IOException ex) {
			System.err.println("Unable to retrieve entries from file: " + ex.getMessage());
			System.exit(2);
		} catch (ValidityCheckFailedException ex) {
			System.err.println("Validity check failed: " + ex.getMessage());
			System.exit(3);
		}

		Command[] commands = {
			new PutCommand(), new GetCommand(), 
			new RemoveCommand(), new ExitCommand()
		};

		Shell<DataBaseState> shell = new Shell<DataBaseState>(commands, state);

		try {
			shell.process(args);
		} catch (WrongCommandException ex) {
			System.err.println(ex.getMessage());
			System.exit(5);
		} catch (CommandFailException ex) {
			System.err.println("error while processing command: " + ex.getMessage());
			System.exit(6);
		} catch (UserInterruptionException ex) {
		}

		try {
			table.write(System.getProperty("fizteh.db.dir"), "db.dat");
		} catch (IOException ex) {
			System.err.println("Unable to store entries in the file: " + ex.getMessage());
			System.exit(7);
		} catch (ValidityCheckFailedException ex) {
			System.err.println("Validity check failed: " + ex.getMessage());
			System.exit(8);
		}

		System.exit(0);
	}
}