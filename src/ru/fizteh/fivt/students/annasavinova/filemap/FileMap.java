package ru.fizteh.fivt.students.annasavinova.filemap;

import ru.fizteh.fivt.students.annasavinova.shell.UserShell;

public class FileMap extends UserShell {
    private DataBaseProvider prov;
    private DataBase currTable;

    public FileMap() {
        try {
            DBaseProviderFactory factory = new DBaseProviderFactory();
            prov = (DataBaseProvider) factory.create(factory.getRoot());
        } catch (RuntimeException e1) {
            System.err.println(e1.getMessage());
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
            currTable.unloadData();
            System.err.println(errStr);
            System.exit(1);
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
                doCommit();
                break;
            case "rollback":
                doRollBack();
                break;
            case "exit":
                if (currTable != null) {
                    currTable.unloadData();
                }
                System.exit(0);
                break;
            default:
                printError("Unknown command");
            }
        }
    }

    private void doCommit() {
        System.out.println(currTable.commit());
    }

    private void doRollBack() {
        System.out.println(currTable.rollback());
    }

    private void doCreateTable(String tableName) {
        try {
            if (prov.createTable(tableName) == null) {
                System.out.println(tableName + " exists");
            } else {
                System.out.println("created");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
        }
    }

    private void doDropTable(String tableName) {
        if (currTable == prov.getTable(tableName)) {
            currTable = null;
        }
        try {
            prov.removeTable(tableName);
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
            String oldValue = currTable.put(key, value);
            if (oldValue == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(oldValue);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void doGet(String key) {
        try {
            String value = currTable.get(key);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(value);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    private void doRemove(String key) {
        try {
            String value = currTable.remove(key);
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
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}
