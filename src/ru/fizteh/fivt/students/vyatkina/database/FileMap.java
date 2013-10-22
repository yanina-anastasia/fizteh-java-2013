package ru.fizteh.fivt.students.vyatkina.database;

import ru.fizteh.fivt.students.vyatkina.database.commands.ExitDatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.GetCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.PutCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.RemoveCommand;
import ru.fizteh.fivt.students.vyatkina.shell.Command;
import ru.fizteh.fivt.students.vyatkina.shell.Shell;
import ru.fizteh.fivt.students.vyatkina.database.SingleTable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class FileMap {

    static Set <Command> getFileMapCommands (SingleTable table) {
        Set commands = new HashSet ();
        commands.add ( new GetCommand (table));
        commands.add ( new PutCommand (table));
        commands.add ( new RemoveCommand (table));
        commands.add ( new ExitDatabaseCommand (table));
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
        try {
        SingleTable table = new SingleTable ("MyLittleTable",databasePath);
        Shell shell;
        if (args.length == 0) {
            shell = new Shell (getFileMapCommands (table), Shell.Mode.INTERACTIVE);
        }
        else {
            shell = new Shell (getFileMapCommands (table), Shell.Mode.PACKET);
        }
        shell.startWork (args);
        } catch (IOException e) {
            System.err.println (e.getMessage ());
            System.exit (-1);
        }

    }

}
