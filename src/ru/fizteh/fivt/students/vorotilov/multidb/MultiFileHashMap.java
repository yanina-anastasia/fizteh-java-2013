package ru.fizteh.fivt.students.vorotilov.multidb;

import ru.fizteh.fivt.students.vorotilov.db.DataBaseOpenFailed;
import ru.fizteh.fivt.students.vorotilov.db.DbInteractiveInput;
import ru.fizteh.fivt.students.vorotilov.db.DbPackageInput;
import ru.fizteh.fivt.students.vorotilov.shell.*;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMap {
    private static boolean interactiveMode;
    private static DataBase currentDataBase;
    private static File homeDirectory;

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
                case "create":
                    if (parsedCommand.length != 2) {
                        System.out.println("put: must get 1 parameters");
                        throw new WrongCommand();
                    } else {
                        File newDb = new File(homeDirectory, parsedCommand[1]);
                        if (newDb.exists()) {
                            System.out.println(parsedCommand[1] + " exists");
                        } else {
                            if (!newDb.mkdir()) {
                                System.out.println("create: can't create new db");
                                throw new WrongCommand();
                            } else {
                                System.out.println("created");
                            }
                        }
                    }
                    break;
                case "use":
                    if (parsedCommand.length != 2) {
                        System.out.println("use: must get 1 parameter");
                        throw new WrongCommand();
                    } else {
                        try {
                            File newDataBase = new File(homeDirectory, parsedCommand[1]);
                            if (currentDataBase != null && newDataBase.equals(currentDataBase.dbDirectory)) {
                                System.out.println(parsedCommand[1] + " is already used");
                            }
                            if (!newDataBase.exists()) {
                                System.out.println(parsedCommand[1] + " not exists");
                                throw new WrongCommand();
                            } else {
                                if (currentDataBase != null) {
                                    currentDataBase.save();
                                    currentDataBase.close();
                                }
                                currentDataBase = new DataBase(newDataBase);
                                System.out.println("using " + parsedCommand[1]);
                            }
                        } catch (DbDirectoryException e) {
                            System.out.println(e.getMessage());
                            System.exit(1);
                        }
                    }
                    break;
                case "drop":
                    if (parsedCommand.length != 2) {
                        System.out.println("drop: must get 1 parameter");
                        throw new WrongCommand();
                    } else {
                        File dbToDrop = new File(homeDirectory, parsedCommand[1]);
                        if (!dbToDrop.exists()) {
                            System.out.println(parsedCommand[1] + " not exists");
                            throw new WrongCommand();
                        } else {
                            if (currentDataBase != null && currentDataBase.dbDirectory.equals(dbToDrop)) {
                                currentDataBase.close();
                                currentDataBase = null;
                            }
                            try {
                                FileUtil.recursiveDelete(homeDirectory, dbToDrop);
                                System.out.println("dropped");
                            } catch (FileWasNotDeleted e) {
                                System.out.println("drop: can't delete file ' "
                                        + e.getProblematicFile().getCanonicalPath() + "'");
                            }
                        }
                    }
                    break;
                case "put":
                    if (parsedCommand.length != 3) {
                        System.out.println("put: must get 2 parameters");
                        throw new WrongCommand();
                    } else if (currentDataBase == null) {
                        System.out.println("no table");
                        throw new WrongCommand();
                    } else {
                        try {
                            currentDataBase.put(parsedCommand[1], parsedCommand[2]);
                        } catch (DataBaseOpenFailed e) {
                            System.out.println("can't create new db file");
                        } catch (DbDirectoryException e) {
                            System.out.println(e.getMessage());
                            System.exit(1);
                        }
                    }
                    break;
                case "get":
                    if (parsedCommand.length != 2) {
                        System.out.println("get: must get 1 parameter");
                        throw new WrongCommand();
                    } else if (currentDataBase == null) {
                        System.out.println("no table");
                        throw new WrongCommand();
                    } else {
                        try {
                            currentDataBase.get(parsedCommand[1]);
                        } catch (DataBaseOpenFailed e) {
                            System.out.println("can't create new db file");
                        } catch (DbDirectoryException e) {
                            System.out.println(e.getMessage());
                            System.exit(1);
                        }
                    }
                    break;
                case "remove":
                    if (parsedCommand.length != 2) {
                        System.out.println("remove: must get 1 parameter");
                        throw new WrongCommand();
                    } else if (currentDataBase == null) {
                        System.out.println("no table");
                        throw new WrongCommand();
                    } else {
                        try {
                            currentDataBase.remove(parsedCommand[1]);
                        } catch (DataBaseOpenFailed e) {
                            System.out.println("can't create new db file");
                        } catch (DbDirectoryException e) {
                            System.out.println(e.getMessage());
                            System.exit(1);
                        }
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
            currentDataBase = null;
            if (System.getProperty("fizteh.db.dir") == null) {
                System.out.println("No db home directory in property");
                System.exit(1);
            } else {
                homeDirectory = new File(System.getProperty("fizteh.db.dir"));
                if (!homeDirectory.exists()) {
                    System.out.println("No db home directory");
                    System.exit(1);
                }
            }
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
            if (currentDataBase != null) {
                currentDataBase.save();
                currentDataBase.close();
            }
            System.exit(0);
        } catch (IOException | WrongCommand e) {
            if (currentDataBase != null) {
                currentDataBase.save();
                currentDataBase.close();
            }
            System.exit(1);
        }
    }

}
