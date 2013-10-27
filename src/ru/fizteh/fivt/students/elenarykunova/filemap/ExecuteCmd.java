package ru.fizteh.fivt.students.elenarykunova.filemap;

import java.io.File;

import ru.fizteh.fivt.students.elenarykunova.shell.Shell;

public class ExecuteCmd extends Shell {

    Filemap mp;

    public ExecuteCmd(String rootDir, Filemap myMap) {
        currPath = new File(rootDir);
        mp = myMap;
    }

    @Override
    public void exitWithError() {
        mp.saveChanges();
        System.exit(1);
    }

    @Override
    protected String[] getArguments(String input) {
        input = input.trim();
        if (input.isEmpty()) {
            return null;
        }
        return input.split("[\\s]+", 3);
    }

    protected DataBase getDataBaseFromKeyAndCheck(String key) {
        int hashcode = Math.abs(key.hashCode());
        int ndir = hashcode % 16;
        int nfile = hashcode / 16 % 16;
        if (!mp.data[ndir][nfile].hasFile()) {
            // doesn't exists. need to create.
            mp.data[ndir][nfile] = new DataBase(mp.currTable, ndir, nfile, true);
        }
        return mp.data[ndir][nfile];
    }

    @Override
    protected ExitCode analyze(String input) {
        String[] arg = getArguments(input);
        if (arg == null || arg.length == 0) {
            return ExitCode.OK;
        }
        String ans;
        switch (arg[0]) {
        case "put":
            if (arg.length == 3) {
                if (mp.currTable == null) {
                    System.out.println("no table");
                    return ExitCode.OK;
                }
                ans = getDataBaseFromKeyAndCheck(arg[1]).put(arg[1], arg[2]);
                if (ans == null) {
                    System.out.println("new");
                } else {
                    System.out.println("overwrite");
                    System.out.println(ans);
                }
                return ExitCode.OK;
            }
            break;
        case "get":
            if (arg.length == 2) {
                if (mp.currTable == null) {
                    System.out.println("no table");
                    return ExitCode.OK;
                }
                ans = getDataBaseFromKeyAndCheck(arg[1]).get(arg[1]);
                if (ans == null) {
                    System.out.println("not found");
                } else {
                    System.out.println("found");
                    System.out.println(ans);
                }
                return ExitCode.OK;
            }
            break;
        case "remove":
            if (arg.length == 2) {
                if (mp.currTable == null) {
                    System.out.println("no table");
                    return ExitCode.OK;
                }
                ans = getDataBaseFromKeyAndCheck(arg[1]).remove(arg[1]);
                if (ans == null) {
                    System.out.println("not found");
                } else {
                    System.out.println("removed");
                }
                return ExitCode.OK;
            }
            break;
        case "create":
            if (arg.length == 2) {
                String tablePath = currPath + File.separator + arg[1];
                File tmpFile = new File(tablePath);
                if (tmpFile.exists()) {
                    System.out.println(arg[1] + " exists");
                } else {
                    if (mkdir(arg[1]) == ExitCode.OK) {
                        System.out.println("created");
                    } else {
                        System.err.println(arg[1] + " can't create a table");
                        return ExitCode.ERR;
                    }
                }
                return ExitCode.OK;
            }
            break;
        case "drop":
            if (arg.length == 2) {
                String tablePath = currPath + File.separator + arg[1];
                File tmpFile = new File(tablePath);
                if (!tmpFile.exists() || !tmpFile.isDirectory()) {
                    System.out.println(arg[1] + " not exists");
                } else {
                    if (rm(arg[1]) == ExitCode.OK) {
                        if (mp.currTable != null && mp.currTable.equals(tablePath)) {
                            mp.currTable = null;
                        }
                        System.out.println("dropped");
                    } else {
                        System.err.println(arg[1] + " can't drop a table");
                        return ExitCode.ERR;
                    }
                }
                return ExitCode.OK;
            }
            break;
        case "use":
            if (arg.length == 2) {
                String tablePath = currPath + File.separator + arg[1];
                File tmpFile = new File(tablePath);
                if (!tmpFile.exists() || !tmpFile.isDirectory()) {
                    System.out.println(arg[1] + " not exists");
                } else {
                    if (!tablePath.equals(mp.currTable)) {
                        mp.changeTable(arg[1]);
                    }
                    System.out.println("using " + arg[1]);
                }
                return ExitCode.OK;
            }
            break;
        case "exit":
            if (arg.length == 1) {
                return ExitCode.EXIT;
            }
            break;
        default:
            System.err.println(arg[0] + ": no such command");
            return ExitCode.ERR;
        }
        System.err.println(arg[0] + ": incorrect number of arguments");
        return ExitCode.ERR;
    }
}
