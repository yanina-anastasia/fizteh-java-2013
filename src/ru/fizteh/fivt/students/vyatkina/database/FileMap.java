package ru.fizteh.fivt.students.vyatkina.database;

import ru.fizteh.fivt.students.vyatkina.database.commands.ExitDatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.GetCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.PutCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.RemoveCommand;
import ru.fizteh.fivt.students.vyatkina.Command;
import ru.fizteh.fivt.students.vyatkina.shell.Shell;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class FileMap {

    static Set<Command> getFileMapCommands (DatabaseState state) {
        Set commands = new HashSet ();
        commands.add (new GetCommand (state));
        commands.add (new PutCommand (state));
        commands.add (new RemoveCommand (state));
        commands.add (new ExitDatabaseCommand (state));
        return commands;
    }

    public static void main (String[] args) {
        String input = System.getProperty ("fizteh.db.dir");
        Path databasePath;
        if (input != null) {
            databasePath = Paths.get (input);
        } else {
            System.err.println ("This is SingleDatabase. To run program, give it propereties -Dfizteh.db.dir=<directory>");
            return;
        }

        SingleTableProviderFactory singleTableProviderFactory = new SingleTableProviderFactory ();
        try {
            SingleTableProvider tableProvider = (SingleTableProvider) singleTableProviderFactory.create (databasePath.toString ());
            Set<Command> commands = getFileMapCommands (tableProvider.state);

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
