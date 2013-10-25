package ru.fizteh.fivt.students.kinanAlsarmini.filemap;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.Shell;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.ExternalCommand;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.ExitCommand;
import java.io.IOException;
import java.io.File;

class FileMap {
    private Table table;
    private File databaseFile;
    private Shell shell;

    public FileMap(String databaseDir, String databaseName) {
        databaseFile = new File(databaseDir, databaseName);

        if (!databaseFile.isAbsolute()) {
            System.err.println("Given directory isn't absolute.");
            System.exit(1);
        }

        if (!databaseFile.exists()) {
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                System.err.println(databaseName + " can't be created.");
                System.exit(1);
            }
        }

        if (!databaseFile.isFile()) {
            System.err.println(databaseName + " isn't a proper file.");
            System.exit(1);
        }

        table = new Table();

        ExternalCommand[] possibleCommands = new ExternalCommand[] {
            new ExitCommand(),
                new PutCommand(table),
                new GetCommand(table),
                new RemoveCommand(table)
        };

        shell = new Shell(possibleCommands);

        try {
            TableReader tableReader = new TableReader(databaseFile);
            tableReader.readTable(table);
            tableReader.close();
        } catch (IOException e) {
            System.err.println(databaseName + " doesn't exist or isn't a proper file.");
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage() + " Argument: " + databaseFile);
            System.exit(1);
        }
    }

    private void writeTable() {
        try {
            TableWriter tableWriter = new TableWriter(databaseFile);
            tableWriter.writeTable(table);
            tableWriter.close();
        } catch (IOException e) {
            System.err.println(databaseFile + " can't be opened for writing.");
            System.exit(1);
        }
    }

    public void startInteractive() {
        shell.startInteractive();
        writeTable();
    }

    public void startBatch(String commands) {
        shell.startBatch(commands);
        writeTable();
    }
}
