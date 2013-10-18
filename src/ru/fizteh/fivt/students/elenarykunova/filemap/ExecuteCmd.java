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
    protected ExitCode analyze(String input) {
        String[] arg = getArguments(input);
        if (arg == null || arg.length == 0) {
            return ExitCode.OK;
        }
        String ans;
        switch (arg[0]) {
        case "put":
            if (arg.length >= 3) {
                StringBuilder secondArg = new StringBuilder(arg[2]);
                for (int i = 3; i < arg.length; i++) {
                    secondArg.append(" ");
                    secondArg.append(arg[i]);
                }
                ans = db.put(arg[1], secondArg.toString());
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
