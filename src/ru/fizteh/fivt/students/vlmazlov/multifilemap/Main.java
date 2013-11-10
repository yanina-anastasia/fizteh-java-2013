package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import ru.fizteh.fivt.students.vlmazlov.shell.Shell;
import ru.fizteh.fivt.students.vlmazlov.shell.WrongCommandException;
import ru.fizteh.fivt.students.vlmazlov.shell.CommandFailException;
import ru.fizteh.fivt.students.vlmazlov.shell.Command;
import ru.fizteh.fivt.students.vlmazlov.shell.UserInterruptionException;
import ru.fizteh.fivt.students.vlmazlov.shell.ExitCommand;
import java.io.IOException;
import java.io.FileNotFoundException;		

public class Main {
	public static void main(String[] args) {

		DataBaseState state = null;
		FileMapProviderFactory factory = new FileMapProviderFactory();

		try {
			state =  new DataBaseState(factory.create(System.getProperty("fizteh.db.dir")));
		} catch (IllegalArgumentException ex) {
			System.err.println(ex.getMessage());
			System.exit(1);
		}

		try {
			DataBaseReader.readMultiTableDataBase((FileMapProvider)state.getProvider());
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
			new DropCommand(), new CommitCommand(),
			new RollBackCommand()
		};

		Shell<DataBaseState> shell = new Shell<DataBaseState>(commands, state);

		try {
			shell.process(args);
		} catch (WrongCommandException ex) {
			System.err.println(ex.getMessage());
			System.exit(5);
		} catch (CommandFailException ex) {
			System.err.println(ex.getMessage());
			System.exit(6);
		} catch (IllegalArgumentException ex) {
			System.err.println(ex.getMessage());
			System.exit(7);
		} catch (UserInterruptionException ex) {
		}

		try {
			DataBaseWriter.writeMultiTableDataBase((FileMapProvider)state.getProvider());
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
			System.exit(8);
		} catch (ValidityCheckFailedException ex) {
			System.err.println("Validity check failed: " + ex.getMessage());
			System.exit(9);
		}
	}
}