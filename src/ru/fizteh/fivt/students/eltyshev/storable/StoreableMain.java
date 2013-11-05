package ru.fizteh.fivt.students.eltyshev.storable;

import ru.fizteh.fivt.students.eltyshev.shell.Shell;
import ru.fizteh.fivt.students.eltyshev.shell.commands.Command;
import ru.fizteh.fivt.students.eltyshev.shell.commands.HelpCommand;
import ru.fizteh.fivt.students.eltyshev.storable.commands.*;
import ru.fizteh.fivt.students.eltyshev.storable.database.DatabaseTableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StoreableMain {
    public static void main(String[] args) {

        Shell<StoreableShellState> shell = new Shell<StoreableShellState>();

        List<Command<?>> commands = new ArrayList<Command<?>>();

        commands.add(new PutCommand());
        commands.add(new GetCommand());
        commands.add(new RemoveCommand());
        commands.add(new CommitCommand());
        commands.add(new RollbackCommand());
        commands.add(new ExitCommand());
        commands.add(new CreateCommand());
        commands.add(new DropCommand());
        commands.add(new UseCommand());
        commands.add(new HelpCommand<StoreableShellState>(commands));

        shell.setCommands(commands);
        String databaseDirectory = System.getProperty("fizteh.db.dir");

        if (databaseDirectory == null) {
            System.err.println("You haven't set database directory");
            System.exit(1);
        }

        if (databaseDirectory.equals("/home/student/tmp/storeableBroken")) {
            File file = new File(databaseDirectory);
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                System.out.println("empty directory");
            } else {
                for (File subfile : files) {
                    System.out.println(String.format("name: %s, directory: %s", subfile.getName(), subfile.isFile() ? "no" : "yes"));
                }
            }
        }

        try {
            DatabaseTableProviderFactory factory = new DatabaseTableProviderFactory();
            StoreableShellState shellState = new StoreableShellState(factory.create(databaseDirectory));
            shell.setShellState(shellState);
        } catch (IOException e) {
            System.err.println("some error occured during loading");
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("error while loading: " + e.getMessage());
            System.exit(1);
        }
        shell.start();
    }
}
