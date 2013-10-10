package ru.fizteh.fivt.students.vorotilov.file_map;

import ru.fizteh.fivt.students.vorotilov.shell.*;

import java.io.File;
import java.io.IOException;

public class FileMapMain {
    private static File currentDataBase;
    private static final String DEFAULT_FILE_NAME = "db.dat";

    private static void processCommand(String[] parsedCommand) throws ExitCommand, IOException {
        switch (parsedCommand[0]) {
            case "exit":
                if (parsedCommand.length > 1) {
                    System.out.println("exit: must not get parameter");
                } else {
                    throw new ExitCommand();
                }
                break;
            case "put":
                if (parsedCommand.length != 2) {
                    System.out.println("put: must get 2 parameters");
                } else {
                    DataBaseCommands.putCommand(currentDataBase, parsedCommand);
                }
                break;
            case "get":
                if (parsedCommand.length != 2) {
                    System.out.println("get: must get 1 parameter");
                } else {
                    DataBaseCommands.getCommand(currentDataBase, parsedCommand);
                }
                break;
            case "remove":
                if (parsedCommand.length > 1) {
                    System.out.println("remove: must get 1 parameter");
                } else {
                    DataBaseCommands.removeCommand(currentDataBase, parsedCommand);
                }
                break;
            default:
                System.out.println(currentDataBase.getCanonicalPath());
                System.out.println("unknown command: '" + parsedCommand[0] + "'");
                break;
        }
    }

    public static void main(String[] args) {
        final boolean interactiveMode = (args.length == 0);
        try {
            currentDataBase = new File(System.getProperty("fizteh.db.dir"), DEFAULT_FILE_NAME).getCanonicalFile();
            ConsoleCommands shellInputCommands;
            if (interactiveMode) {
                shellInputCommands = new InteractiveCommands();
            } else {
                shellInputCommands = new PackageCommands(args);
            }
            while (true) {
                processCommand(shellInputCommands.getNextCommand());
            }
        } catch (ExitCommand | NoNextCommand e) {
            System.exit(0);
        } catch (IOException e) {
            System.exit(1);
        }
    }

}
