package ru.fizteh.fivt.students.vorotilov.db;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.vorotilov.shell.*;

import java.io.File;
import java.io.IOException;

public class TableMain {
    private static boolean interactiveMode;
    private static VorotilovTableProvider tableProvider;
    private static VorotilovTable currentTable;

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
                        Table newTable = tableProvider.createTable(parsedCommand[1]);
                        if (newTable == null) {
                            System.out.println(parsedCommand[1] + " exists");
                        } else {
                            System.out.println("created");
                        }
                    }
                    break;
                case "use":
                    if (parsedCommand.length != 2) {
                        System.out.println("use: must get 1 parameter");
                        throw new WrongCommand();
                    } else {
                        VorotilovTable newTable = tableProvider.getTable(parsedCommand[1]);
                        if (newTable == null) {
                            System.out.println(parsedCommand[1] + " not exists");
                            throw new WrongCommand();
                        } else {
                            if (currentTable != null) {
                                if (currentTable.getName().equals(newTable.getName())) {
                                    System.out.println(parsedCommand[1] + " is already used");
                                } else {
                                    currentTable.commit();
                                    try {
                                        currentTable.close();
                                    } catch (Exception e) {
                                        throw new IllegalStateException("Can't close table");
                                    }
                                }
                            }
                            currentTable = newTable;
                            System.out.println("using " + parsedCommand[1]);
                        }
                    }
                    break;
                case "drop":
                    if (parsedCommand.length != 2) {
                        System.out.println("drop: must get 1 parameter");
                        throw new WrongCommand();
                    } else {
                        File tablePath = new File(tableProvider.getRoot(), parsedCommand[1]);
                        if (!tablePath.exists()) {
                            System.out.println(parsedCommand[1] + " not exists");
                            throw new WrongCommand();
                        } else {
                            tableProvider.removeTable(parsedCommand[1]);
                            System.out.println("dropped");
                        }
                    }
                    break;
                case "put":
                    if (parsedCommand.length != 3) {
                        System.out.println("put: must get 2 parameters");
                        throw new WrongCommand();
                    } else if (currentTable == null) {
                        System.out.println("no table");
                        throw new WrongCommand();
                    } else {
                        String value = currentTable.put(parsedCommand[1], parsedCommand[2]);
                        if (value == null) {
                            System.out.println("new");
                        } else {
                            System.out.println("overwrite\n" + value);
                        }
                    }
                    break;
                case "get":
                    if (parsedCommand.length != 2) {
                        System.out.println("get: must get 1 parameter");
                        throw new WrongCommand();
                    } else if (currentTable == null) {
                        System.out.println("no table");
                        throw new WrongCommand();
                    } else {
                        String value = currentTable.get(parsedCommand[1]);
                        if (value != null) {
                            System.out.println("found\n" + value);
                        } else {
                            System.out.println("not found");
                        }
                    }
                    break;
                case "remove":
                    if (parsedCommand.length != 2) {
                        System.out.println("remove: must get 1 parameter");
                        throw new WrongCommand();
                    } else if (currentTable == null) {
                        System.out.println("no table");
                        throw new WrongCommand();
                    } else {
                        String value = currentTable.remove(parsedCommand[1]);
                        if (value != null) {
                            System.out.println("removed");
                        } else {
                            System.out.println("not found");
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
            VorotilovTableProviderFactory tableProviderFactory = new VorotilovTableProviderFactory();
            String rootDir = System.getProperty("fizteh.db.dir");
            if (rootDir == null) {
                throw new IllegalArgumentException("Property is null");
            }
            tableProvider = tableProviderFactory.create(rootDir);
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
            if (currentTable != null) {
                currentTable.commit();
                try {
                    currentTable.close();
                } catch (Exception f) {
                    throw new RuntimeException();
                }
            }
            System.exit(0);
        } catch (IOException | WrongCommand e) {
            currentTable.commit();
            try {
                currentTable.close();
            } catch (Exception f) {
                throw new RuntimeException();
            }
            System.exit(1);
        }
    }

}
