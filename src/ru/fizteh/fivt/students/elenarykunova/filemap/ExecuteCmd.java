package ru.fizteh.fivt.students.elenarykunova.filemap;

import ru.fizteh.fivt.students.elenarykunova.shell.Shell;

public class ExecuteCmd extends Shell {

    DataBase db;

    public ExecuteCmd(DataBase argDb) {
        db = argDb;
    }

    @Override
    public void exitWithError() {
        db.commitChanges();
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
                ans = db.put(arg[1], arg[2]);
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
                ans = db.get(arg[1]);
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
                ans = db.remove(arg[1]);
                if (ans == null) {
                    System.out.println("not found");
                } else {
                    System.out.println("removed");
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
