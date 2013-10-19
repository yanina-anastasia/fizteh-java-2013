package ru.fizteh.fivt.students.kamilTalipov.database;

import ru.fizteh.fivt.students.kamilTalipov.shell.Command;
import ru.fizteh.fivt.students.kamilTalipov.shell.Shell;
import ru.fizteh.fivt.students.kamilTalipov.shell.Exit;

import java.io.FileNotFoundException;

public class DatabaseRunner {
    public static void main(String[] args) {
        Database database = null;
        try {
            database = new SimpleDatabase(System.getProperty("fizteh.db.dir"));
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        Command[] commands = new Command[] {new PutCommand(database),
                new GetCommand(database),
                new RemoveCommand(database),
                new Exit()};
        try {
            Shell.run(commands, args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } finally {
            database.exit();
        }
    }
}
