package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.FileNotFoundException;
import java.io.IOException;

import ru.fizteh.fivt.students.surakshina.shell.Shell;

public class Commands extends Shell {
    @Override
    protected String[] extractArgumentsFromInputString(String input) {
        input = input.trim();
        if (input.isEmpty()) {
            return null;
        }
        return input.split("[\\s]+", 3);
    }

    @Override
    protected String rewriteInput(String current) {
        return current;
    }

    @Override
    public void executeProcess(String[] input) {
        if (input == null) {
            return;
        }
        switch (input[0]) {
        case "put":
            if (input.length == 3) {
                put(input[1], input[2]);
            } else {
                printError("Incorrect number of arguments");
            }
            break;
        case "get":
            if (input.length == 2) {
                get(input[1]);
            } else {
                printError("Incorrect number of arguments");
            }
            break;
        case "remove":
            if (input.length == 2) {
                remove(input[1]);
            } else {
                printError("Incorrect number of arguments");
            }
            break;
        default:
            printError("Incorrect input");
        }
    }

    @Override
    public void printError(String s) {
        if (isInteractive) {
            System.out.println(s);
        } else {
            System.err.println(s);
            try {
                FileMap.writeInDatabase();
            } catch (FileNotFoundException e) {
                System.err.println("Can't read database");
                FileMap.closeFile(FileMap.dataBase);
                System.exit(1);
            } catch (IOException e1) {
                System.err.println("Can't write in database");
                FileMap.closeFile(FileMap.dataBase);
                System.exit(1);
            }
            FileMap.closeFile(FileMap.dataBase);
            System.exit(1);
        }
    }

    private static void put(String key, String value) {
        if (FileMap.fileMap.containsKey(key)) {
            if (FileMap.fileMap.get(key) != null) {
                System.out.println("overwrite\n" + FileMap.fileMap.get(key));
                FileMap.fileMap.put(key, value);
            } else {
                System.out.println("new");
            }
        } else {
            FileMap.fileMap.put(key, value);
            System.out.println("new");
        }
    }

    private static void get(String key) {
        if (FileMap.fileMap.containsKey(key)) {
            System.out.println("found\n" + FileMap.fileMap.get(key));
        } else {
            System.out.println("not found");
        }
    }

    private static void remove(String key) {
        if (FileMap.fileMap.containsKey(key)) {
            FileMap.fileMap.remove(key);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }

}
