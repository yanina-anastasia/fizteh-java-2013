package ru.fizteh.fivt.students.lizaignatyeva.database;


import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;
import ru.fizteh.fivt.students.lizaignatyeva.shell.CommandFactory;
import ru.fizteh.fivt.students.lizaignatyeva.shell.CommandRunner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

public class DbMain {
    static Path path;
    static CommandFactory factory;

    private static Hashtable<String, Database> databases;
    private static Hashtable<String, Command> commandsMap;

    //static String currentDatabaseName;
    static Database currentDatabase;

    public static Database getCurrentDatabase() throws Exception {
        //return databases.get(currentDatabaseName); MULTI ONLY
        return currentDatabase;
    }

    public  static void setCurrentDatabaseName(String name) {
        //currentDatabaseName = name;
    }

    public static String concatenateWithDelimiter(String[] strings, String delimiter) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            builder.append(strings[i]);
            if (i != strings.length - 1) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }

    private static void addCommands() {
        commandsMap = new Hashtable<String, Command>();
        commandsMap.put("put", new PutCommand());
        commandsMap.put("get", new GetCommand());
        commandsMap.put("remove", new RemoveCommand());
        commandsMap.put("exit", new ExitCommand());
    }

    public static void main(String[] args) {
        //TODO: remove that for MULTI
        try {
            path = Paths.get(System.getProperty("fizteh.db.dir"));
            if (!Files.isDirectory(path)) {
                throw new Exception(path + " doesn't exist or is not a directory");
            }
        } catch (Exception e) {
            System.out.println("Error opening database: " + e.getMessage());
            System.exit(1);
        }

        path = path.resolve("db.dat");

        currentDatabase = new Database(path.toString()); // SIMPLE ONLY
        addCommands();
        CommandRunner runner = new CommandRunner(path.toFile(), commandsMap);
        runner.run(args);

    }
}
