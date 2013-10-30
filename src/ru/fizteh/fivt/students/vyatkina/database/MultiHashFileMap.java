package ru.fizteh.fivt.students.vyatkina.database;

import ru.fizteh.fivt.students.vyatkina.Command;
import ru.fizteh.fivt.students.vyatkina.database.providerCommands.CreateCommand;
import ru.fizteh.fivt.students.vyatkina.database.providerCommands.DropCommand;
import ru.fizteh.fivt.students.vyatkina.database.providers.MultiTableProvider;
import ru.fizteh.fivt.students.vyatkina.database.providers.MultiTableProviderFactory;
import ru.fizteh.fivt.students.vyatkina.database.tableCommands.ExitDatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.providerCommands.UseCommand;
import ru.fizteh.fivt.students.vyatkina.database.tableCommands.GetCommand;
import ru.fizteh.fivt.students.vyatkina.database.tableCommands.PutCommand;
import ru.fizteh.fivt.students.vyatkina.database.tableCommands.RemoveCommand;
import ru.fizteh.fivt.students.vyatkina.shell.Shell;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class MultiHashFileMap {

    static Set<Command> getMultiHashFileMapCommands (DatabaseState state) {
        Set commands = new HashSet ();
        commands.add (new GetCommand (state));
        commands.add (new PutCommand (state));
        commands.add (new RemoveCommand (state));
        commands.add (new ExitDatabaseCommand (state));
        commands.add (new CreateCommand (state));
        commands.add (new DropCommand (state));
        commands.add (new UseCommand (state));
        return commands;
    }

    public static void main (String[] args) {
        String input = System.getProperty ("fizteh.db.dir");
        Path databasePath;
        if (input != null) {
            databasePath = Paths.get (input);
        } else {
            System.err.println ("This is MultiDatabase. To run program, give it empty directory -Dfizteh.db.dir=<directory>");
            return;
        }

        MultiTableProviderFactory multiTableProviderFactory = new MultiTableProviderFactory ();
        try {
            MultiTableProvider tableProvider = (MultiTableProvider) multiTableProviderFactory.create (databasePath.toString ());
            Set<Command> commands = getMultiHashFileMapCommands (tableProvider.state);

            Shell shell;

            if (args.length == 0) {
                shell = new Shell (commands, Shell.Mode.INTERACTIVE, tableProvider.state);
            } else {
                shell = new Shell (commands, Shell.Mode.PACKET, tableProvider.state);
            }
            shell.startWork (args);
        }
        catch (IllegalArgumentException e) {
            System.err.println (e.getMessage ());
        }

    }


}
