package ru.fizteh.fivt.students.vyatkina.database;

import ru.fizteh.fivt.students.vyatkina.database.commands.ExitDatabaseCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.GetCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.PutCommand;
import ru.fizteh.fivt.students.vyatkina.database.commands.RemoveCommand;
import ru.fizteh.fivt.students.vyatkina.shell.Command;
import ru.fizteh.fivt.students.vyatkina.shell.Shell;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class FileMap {

    static Set <Command> getFileMapCommands (Table table) {
        Set commands = new HashSet ();
        commands.add ( new GetCommand (table));
        commands.add ( new PutCommand (table));
        commands.add ( new RemoveCommand (table));
        commands.add ( new ExitDatabaseCommand (table));
        return commands;
    }

    public static void main (String[] args) {
        Path directory = Paths.get (System.getProperty ("fizteh.db.dir"));
        if (directory == null) {
            directory = Paths.get ("");
        }
        Table table = new SingleTable ("MyLittleTable",directory);

        Shell shell;
        if (args.length == 0) {
            shell = new Shell (getFileMapCommands (table), Shell.Mode.INTERACTIVE);
        }
        else {
            shell = new Shell (getFileMapCommands (table), Shell.Mode.PACKET);
        }
        shell.startWork (args);

    }

}
