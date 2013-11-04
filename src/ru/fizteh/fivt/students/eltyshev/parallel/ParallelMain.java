package ru.fizteh.fivt.students.eltyshev.parallel;

import ru.fizteh.fivt.students.eltyshev.parallel.database.ThreadSafeDatabaseTableProviderFactory;
import ru.fizteh.fivt.students.eltyshev.shell.Shell;
import ru.fizteh.fivt.students.eltyshev.shell.commands.Command;
import ru.fizteh.fivt.students.eltyshev.shell.commands.HelpCommand;
import ru.fizteh.fivt.students.eltyshev.storable.StoreableShellState;
import ru.fizteh.fivt.students.eltyshev.storable.commands.*;
import ru.fizteh.fivt.students.eltyshev.storable.database.DatabaseTableProviderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParallelMain {
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
        try {
            ThreadSafeDatabaseTableProviderFactory factory = new ThreadSafeDatabaseTableProviderFactory();
            StoreableShellState shellState = new StoreableShellState(factory.create(databaseDirectory));
            shell.setShellState(shellState);
        } catch (IOException e) {
            System.err.println("some error occurred during loading");
            System.exit(1);
        }
        shell.start();
    }
}
