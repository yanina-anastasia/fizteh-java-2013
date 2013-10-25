package ru.fizteh.fivt.students.inaumov.filemap;

import ru.fizteh.fivt.students.inaumov.common.CommonShell;
import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		CommonShell fileMapShell = new CommonShell();

		SingleFileMapShellState fileMapState = new SingleFileMapShellState();
		String directory = System.getProperty("fizteh.db.dir");

        try {
			fileMapState.table = new SingleFileTable(directory, "DataBase");
		} catch (IOException exception) {
			System.err.println(exception.getMessage());
			System.exit(1);
		} catch (IllegalArgumentException exception) {
			System.err.println(exception.getMessage());
			System.exit(1);
		}  catch (WrongFileFormatException exception) {
            System.err.println(exception.getMessage());
            System.exit(1);
        }

		fileMapShell.setFileMapState(fileMapState);

		fileMapShell.addCommand(new PutCommand());
		fileMapShell.addCommand(new GetCommand());
		fileMapShell.addCommand(new RemoveCommand());
		fileMapShell.addCommand(new ExitCommand());
		fileMapShell.addCommand(new CommitCommand());
		fileMapShell.addCommand(new RollbackCommand());
		fileMapShell.addCommand(new SizeCommand());

        if (args.length == 0) {
		    fileMapShell.interactiveMode();
        } else {
            fileMapShell.batchMode(args);
        }

        return;
	}
}
