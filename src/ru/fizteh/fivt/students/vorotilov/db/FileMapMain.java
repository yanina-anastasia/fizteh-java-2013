package ru.fizteh.fivt.students.vorotilov.db;

import ru.fizteh.fivt.students.vorotilov.shell.*;

import java.io.File;
import java.io.IOException;

public class FileMapMain {
    private static boolean interactiveMode;
    private static DataBaseFile currentDataBase;
    private static final String DEFAULT_FILE_NAME = "db.dat";

    private static void processCommand(String[] parsedCommand) throws ExitCommand, IOException, WrongCommand {
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
                    currentDataBase.put(parsedCommand[1].getBytes(), parsedCommand[2].getBytes());
                }
                break;
            case "get":
                if (parsedCommand.length != 2) {
                    System.out.println("get: must get 1 parameter");
                } else {
                    currentDataBase.get(parsedCommand[1].getBytes());
                }
                break;
            case "remove":
                if (parsedCommand.length > 1) {
                    System.out.println("remove: must get 1 parameter");
                } else {
                    currentDataBase.remove(parsedCommand[1].getBytes());
                }
                break;
            default:
                System.out.println("unknown command: '" + parsedCommand[0] + "'");
                if (!interactiveMode) {
                    throw new WrongCommand();
                }
                break;
        }
    }

    public static void main(String[] args) {
        interactiveMode = (args.length == 0);
        try {
            File currentFile = new File(System.getProperty("fizteh.db.dir"), DEFAULT_FILE_NAME).getCanonicalFile();
            currentDataBase = new DataBaseFile(currentFile);
            ConsoleInput shellInputCommands;
            if (interactiveMode) {
                shellInputCommands = new InteractiveInput();
            } else {
                shellInputCommands = new PackageInput(args);
            }
            while (shellInputCommands.hasNext()) {
                processCommand(shellInputCommands.getNext());
            }
            throw new WrongCommand();
        } catch (ExitCommand | NoNextCommand e) {
            System.exit(0);
        } catch (IOException | WrongCommand e) {
            System.exit(1);
        }
    }

}
