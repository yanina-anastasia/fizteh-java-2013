package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.shell.Shell;
import ru.fizteh.fivt.students.vlmazlov.shell.WrongCommandException;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import ru.fizteh.fivt.students.vlmazlov.shell.Command;
import ru.fizteh.fivt.students.vlmazlov.shell.UserInterruptionException;
import ru.fizteh.fivt.students.vlmazlov.shell.ExitCommand;
import java.io.IOException;
import java.io.FileNotFoundException;
import ru.fizteh.fivt.students.vlmazlov.filemap.GetCommand;
import ru.fizteh.fivt.students.vlmazlov.filemap.PutCommand;
import ru.fizteh.fivt.students.vlmazlov.filemap.RemoveCommand;

public class Main {
	public static void main(String[] args) throws IOException {

		MultiTableDataBase multiTableDataBase = null;

		try {
			multiTableDataBase = new MultiTableDataBase(System.getProperty("fizteh.db.dir"));
		} catch (FileNotFoundException ex) {
			System.err.println(ex.getMessage());
			System.exit(1);
		} catch (ValidityCheckFailedException ex) {
			System.err.println("Validity check failed: " + ex.getMessage());
			System.exit(2);
		}

		try {
			DataBaseReader.readMultiTableDataBase(multiTableDataBase);
		} catch (IOException ex) {
			System.err.println("Unable to retrieve database: " + ex.getMessage());
			System.exit(3);
		} catch (ValidityCheckFailedException ex) {
			System.err.println("Validity check failed: " + ex.getMessage());
			System.exit(4);
		}

		Command[] commands = {
			new GetCommand(), new PutCommand(), 
			new RemoveCommand(), new ExitCommand(),
			new UseCommand(), new CreateCommand(),
			new DropCommand()
		};

		Shell<MultiTableDataBase> shell = new Shell<MultiTableDataBase>(commands, multiTableDataBase);

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
			DataBaseWriter.writeMultiTableDataBase(multiTableDataBase);
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
			System.exit(7);
		} catch (ValidityCheckFailedException ex) {
			System.err.println("Validity check failed: " + ex.getMessage());
			System.exit(8);
		}
	}
}