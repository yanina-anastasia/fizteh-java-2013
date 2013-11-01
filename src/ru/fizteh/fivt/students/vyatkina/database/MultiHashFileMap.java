package ru.fizteh.fivt.students.vyatkina.database;

import ru.fizteh.fivt.students.vyatkina.Command;
import ru.fizteh.fivt.students.vyatkina.database.commands.CreateCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.DropCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.CommitCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.ExitDatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.UseCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.GetCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.PutCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.RemoveCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.RollbackCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.SizeCommand;
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
        commands.add (new CommitCommand (state));
        commands.add (new SizeCommand (state));
        commands.add (new RollbackCommand (state));

        return commands;
    }

    public static void main (String[] args) {
        String input = System.getProperty ("fizteh.db.dir");
        Path databasePath;
        if (input != null) {
            databasePath = Paths.get (input);
        } else {
           databasePath = null;
           System.exit (-1);
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
