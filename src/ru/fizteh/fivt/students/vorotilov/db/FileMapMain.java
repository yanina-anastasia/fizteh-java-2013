package ru.fizteh.fivt.students.vorotilov.db;

import ru.fizteh.fivt.students.vorotilov.shell.*;

import java.io.File;
import java.io.IOException;

public class FileMapMain {
    private static boolean interactiveMode;
    private static DataBaseFile currentDataBase;
    private static final String DEFAULT_FILE_NAME = "db.dat";

    private static void processCommand(String[] parsedCommand) throws ExitCommand, IOException, WrongCommand {
        try {
            switch (parsedCommand[0]) {
                case "exit":
                    if (parsedCommand.length > 1) {
                        System.out.println("exit: must not get parameter");
                        throw new WrongCommand();
                    } else {
                        throw new ExitCommand();
                    }
                case "put":
                    if (parsedCommand.length != 3) {
                        System.out.println("put: must get 2 parameters");
                        throw new WrongCommand();
                    } else {
                        currentDataBase.put(parsedCommand[1], parsedCommand[2]);
                    }
                    break;
                case "get":
                    if (parsedCommand.length != 2) {
                        System.out.println("get: must get 1 parameter");
                        throw new WrongCommand();
                    } else {
                        currentDataBase.get(parsedCommand[1]);
                    }
                    break;
                case "remove":
                    if (parsedCommand.length != 2) {
                        System.out.println("remove: must get 1 parameter");
                        throw new WrongCommand();
                    } else {
                        currentDataBase.remove(parsedCommand[1]);
                    }
                    break;
                case "":
                    throw new WrongCommand();
                default:
                    System.out.println("unknown command: '" + parsedCommand[0] + "'");
                    throw new WrongCommand();
            }
        } catch (WrongCommand e) {
            if (!interactiveMode) {
                throw e;
            }
        }
    }

    public static void main(String[] args) {
        interactiveMode = (args.length == 0);
        try {
            if (System.getProperty("fizteh.db.dir") == null) {
                System.out.println("No db directory");
                System.exit(1);
            } else {
                File tempFile = new File(System.getProperty("fizteh.db.dir"));
                if (!tempFile.exists()) {
                    System.out.println("No db directory");
                    System.exit(1);
                }
            }
            File currentFile = new File(System.getProperty("fizteh.db.dir"), DEFAULT_FILE_NAME).getCanonicalFile();
            currentDataBase = new DataBaseFile(currentFile);
            ConsoleInput shellInputCommands;
            if (interactiveMode) {
                shellInputCommands = new DbInteractiveInput();
            } else {
                shellInputCommands = new DbPackageInput(args);
            }
            while (shellInputCommands.hasNext()) {
                processCommand(shellInputCommands.getNext());
            }
        } catch (ExitCommand | NoNextCommand e) {
            currentDataBase.close();
            System.exit(0);
        } catch (IOException | WrongCommand | DataBaseOpenFailed e) {
            currentDataBase.close();
            System.exit(1);
        }
    }

}
