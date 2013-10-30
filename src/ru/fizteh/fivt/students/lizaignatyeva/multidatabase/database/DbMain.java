package ru.fizteh.fivt.students.lizaignatyeva.multidatabase.database;


import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;
import ru.fizteh.fivt.students.lizaignatyeva.shell.CommandFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.StringTokenizer;

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
        factory = new CommandFactory(commandsMap);
        if (args.length != 0) {
            String commands = concatenateWithDelimiter(args, " ");
            runCommands(commands);
        } else {
            Scanner input = new Scanner(System.in);
            while (true) {
                try {
                    System.out.print("$ ");
                } catch (Exception e) {
                    System.err.println("Something went wrong!");
                    return;
                }
                String commands = input.nextLine();
                if (commands.length() != 0) {
                    runCommands(commands);
                }
            }
        }

    }

    public static void runCommands(String commands) {
        String[] commandsList = commands.split(";");
        for (String commandWithArguments: commandsList) {
            String[] tokens = tokenizeCommand(commandWithArguments);
            if (tokens.length == 0) {
                continue;
            }
            Command command;
            try {
                command = factory.makeCommand(tokens[0]);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                return;
            }
            try {
                command.run(Arrays.copyOfRange(tokens, 1, tokens.length));
            } catch (Exception e) {
                System.err.println(command.name + ": " + e.getMessage());
                return;
            }
        }
    }

    public static String[] tokenizeCommand(String s) {
        s = s.trim();
        StringTokenizer tokenizer = new StringTokenizer(s);
        String[] result = new String[tokenizer.countTokens()];
        for (int i = 0; tokenizer.hasMoreTokens(); ++i) {
            result[i] = tokenizer.nextToken();
        }
        return result;
    }
}
