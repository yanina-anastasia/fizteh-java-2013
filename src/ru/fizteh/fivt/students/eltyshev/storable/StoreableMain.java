package ru.fizteh.fivt.students.eltyshev.storable;

import ru.fizteh.fivt.students.eltyshev.shell.Shell;
import ru.fizteh.fivt.students.eltyshev.shell.commands.Command;
import ru.fizteh.fivt.students.eltyshev.shell.commands.HelpCommand;
import ru.fizteh.fivt.students.eltyshev.storable.commands.*;
import ru.fizteh.fivt.students.eltyshev.storable.database.DatabaseTableProviderFactory;

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
        DatabaseTableProviderFactory factory = new DatabaseTableProviderFactory();
        StoreableShellState shellState = new StoreableShellState(factory.create(databaseDirectory));
        shell.setShellState(shellState);
        shell.start();
    }
}
