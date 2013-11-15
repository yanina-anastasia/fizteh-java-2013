package ru.fizteh.fivt.students.annasavinova.filemap;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.annasavinova.shell.UserShell;

public class FileMap extends UserShell {
    private DataBaseProvider prov;
    private DataBase currTable;

    private static String root = "";

    public FileMap() {
        if (System.getProperty("fizteh.db.dir") == null) {
            throw new RuntimeException("root dir not selected");
        }
        File r = new File(System.getProperty("fizteh.db.dir"));
        if (!r.exists()) {
            if (!r.mkdir()) {
                throw new RuntimeException("cannot create root dir");
            }
        }
        if (System.getProperty("fizteh.db.dir").endsWith(File.separator)) {
            root = System.getProperty("fizteh.db.dir");
        } else {
            root = System.getProperty("fizteh.db.dir") + File.separatorChar;
        }
        DBaseProviderFactory factory = new DBaseProviderFactory();
        try {
            prov = (DataBaseProvider) factory.create(root);
        } catch (IllegalArgumentException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public String appendArgs(int num, String[] args) {
        StringBuffer str = new StringBuffer(args[num]);
        for (int i = num + 1; i < args.length; ++i) {
            str.append(" ");
            str.append(args[i]);
        }
        return str.toString();
    }

    @Override
    public void printError(String errStr) {
        if (isPacket) {
            try {
                currTable.unloadData();
            } finally {
                System.err.println(errStr);
                System.exit(1);
            }
        } else {
            System.out.println(errStr);
        }
    }

    @Override
    public String[] getArgsFromString(String str) {
        str = str.trim();
        if (str != null) {
            return str.split("[\\s]+", 3);
        } else {
            return null;
        }
    }

    @Override
    protected void execProc(String[] args) {
        if (args != null && args.length != 0) {
            switch (args[0]) {
            case "create":
                if (args.length > 1) {
                    doCreateTable(appendArgs(1, args));
                } else {
                    printError("Incorrect number of args");
                }
                break;
            case "drop":
                if (args.length > 1) {
                    doDropTable(appendArgs(1, args));
                } else {
                    printError("Incorrect number of args");
                }
                break;
            case "use":
                if (args.length > 1) {
                    doUseTable(appendArgs(1, args));
                } else {
                    printError("Incorrect number of args");
                }
                break;
            case "put":
                if (currTable == null) {
                    System.out.println("no table");
                } else {
                    if (args.length > 2) {
                        doPut(args[1], appendArgs(2, args));
                    } else {
                        printError("Incorrect number of args");
                    }
                }
                break;
            case "get":
                if (currTable == null) {
                    System.out.println("no table");
                } else {
                    if (checkArgs(2, args)) {
                        doGet(args[1]);
                    }
                }
                break;
            case "remove":
                if (currTable == null) {
                    System.out.println("no table");
                } else {
                    if (checkArgs(2, args)) {
                        doRemove(args[1]);
                    }
                }
                break;
            case "size":
                System.out.println(currTable.size());
                break;
            case "commit":
                if (checkArgs(1, args)) {
                    doCommit();
                }
                break;
            case "rollback":
                if (checkArgs(1, args)) {
                    doRollBack();
                }
                break;
            case "exit":
                if (checkArgs(1, args)) {
                    doExit();
                }
                break;
            default:
                printError("Unknown command");
            }
        }
    }

    private void doExit() {
        if (currTable != null) {
            currTable.unloadData();
        }
        System.exit(0);
    }

    private void doCommit() {
        try {
            System.out.println(currTable.commit());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void doRollBack() {
        System.out.println(currTable.rollback());
    }

    private void doCreateTable(String args) {
        try {
            args.trim();
            if (!args.endsWith(")")) {
                printError("Incorrect arguments. Need types");
                return;
            }
            args = args.replace(")", "");
            String[] strArray = args.split("[ ]+[/(]", 2);
            if (strArray.length < 2) {
                printError("Incorrect arguments. Need types");
                return;
            }
            String tableName = strArray[0];
            ArrayList<Class<?>> list = new ArrayList<>();
            Scanner sc = new Scanner(strArray[1]);
            while (sc.hasNext()) {
                try {
                    String type = sc.next();
                    Class<?> cl = prov.getClassFromString(type);
                    list.add(cl);
                } catch (RuntimeException e) {
                    printError("wrong type " + e.getMessage());
                    sc.close();
                    return;
                }
            }
            sc.close();
            try {
                if (prov.createTable(tableName, list) == null) {
                    System.out.println(tableName + " exists");
                } else {
                    System.out.println("created");
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            } catch (IllegalArgumentException e) {
                System.out.println("wrong type(" + e.getMessage() + ")");
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private void doUseTable(String tableName) {
        if (currTable != null) {
            int changes = currTable.countChanges();
            if (changes != 0) {
                System.out.println(changes + " unsaved changes");
                return;
            }
        }
        try {
            if (prov.getTable(tableName) == null) {
                System.out.println(tableName + " not exists");
            } else {
                if (currTable != null) {
                    currTable.unloadData();
                }
                currTable = (DataBase) prov.getTable(tableName);
                System.out.println("using " + tableName);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (RuntimeException e) {
            printError(e.getMessage());
        }
    }

    private void doDropTable(String tableName) {
        if (currTable == prov.getTable(tableName)) {
            currTable = null;
        }
        try {
            try {
                prov.removeTable(tableName);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
            System.out.println("dropped");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println(tableName + " not exists");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void doPut(String key, String value) {
        try {
            Storeable oldValue = currTable.put(key, prov.deserialize(currTable, value));
            if (oldValue == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(oldValue.getStringAt(0));
            }
        } catch (ParseException e) {
            printError("Cannot parse arguments");
        } catch (RuntimeException e) {
            printError(e.getMessage());
        }

    }

    private void doGet(String key) {
        try {
            Storeable value = currTable.get(key);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(prov.serialize(currTable, value));
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    private void doRemove(String key) {
        try {
            Storeable value = currTable.remove(key);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            FileMap data = new FileMap();
            data.exec(args);
            data.doExit();
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}
