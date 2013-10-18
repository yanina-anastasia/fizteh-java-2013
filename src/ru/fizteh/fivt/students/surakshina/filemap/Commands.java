package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Commands {
    public static void executeProcess(String[] input) {
        switch (input[0]) {
        case "put":
            if (input.length >= 3) {
                put(input);
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

    public static void printError(String s) {
        if (WorkWithInput.isInteractive) {
            System.out.println(s);
        } else {
            System.err.println(s);
            try {
                FileMap.writeInDatabase();
            } catch (FileNotFoundException e) {
                System.err.println("Can't read database");
                System.exit(1);
            } catch (IOException e1) {
                System.err.println("Can't write in database");
                System.exit(1);
            }
            try {
                FileMap.dataBase.close();
            } catch (IOException e2) {
                System.err.println("Can't close a database");
                System.exit(1);
            }
            System.exit(1);
        }
    }

    private static void put(String[] input) {
        String key = input[1];
        StringBuffer tmp = new StringBuffer();
        for (int i = 2; i < input.length; ++i) {
            tmp = tmp.append(input[i]);
            tmp = tmp.append(" ");
        }
        String value = tmp.toString();
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
