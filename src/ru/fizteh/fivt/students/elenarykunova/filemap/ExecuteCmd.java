package ru.fizteh.fivt.students.elenarykunova.filemap;

import java.io.IOException;
import java.util.ArrayList;
import java.text.ParseException;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.elenarykunova.shell.Shell;

public class ExecuteCmd extends Shell {

    private Filemap mp;
    private MyTableProvider mtp;
    
    
    public ExecuteCmd(Filemap myMap, MyTableProvider myProvider) {
        mp = myMap;
        mtp = myProvider;
    }

    @Override
    public void exitWithError() {
        try {
            mp.saveChanges();
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
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


    private ArrayList<Class<?>> getTypes(String arg) throws IOException {
        arg = arg.trim();
        arg = arg.replaceAll("[\\s]+", " ");
        arg = arg.replace("(", "");
        arg = arg.replace(")", "");
        arg = arg.replace(",", "");
        String[] array = arg.split(" ");
        ArrayList<Class<?>> types = new ArrayList<Class<?>>();
        for (int i = 2; i < array.length; i++) {
            types.add(mp.getTypeFromString(array[i]));
        }
        return types;
    }
    
    
    @Override
    protected ExitCode analyze(String input) {
        String[] arg = getArguments(input);
        if (arg == null || arg.length == 0) {
            return ExitCode.OK;
        }
        Storeable ans;
        switch (arg[0]) {
        case "put":
            if (arg.length == 3) {
                if (mp.getName() == null) {
                    System.out.println("no table");
                    return ExitCode.OK;
                }
                try {
                    ans = mp.put(arg[1], mtp.deserialize(mp, input.substring(input.indexOf("["))));
                } catch (ColumnFormatException e2) {
                    System.err.println("wrong type (" + e2.getMessage() + ")");
                    return ExitCode.ERR;
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                    return ExitCode.ERR;
                } catch (ParseException e1) {
                    System.err.println(e1.getMessage());
                    return ExitCode.ERR;                    
                }
                if (ans == null) {
                    System.out.println("new");
                } else {
                    System.out.println("overwrite");
                    System.out.println(mtp.serialize(mp, ans));
                }
                return ExitCode.OK;
            }
            break;
        case "get":
            if (arg.length == 2) {
                if (mp.getName() == null) {
                    System.out.println("no table");
                    return ExitCode.OK;
                }
                try {
                    ans = mp.get(arg[1]);
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                    return ExitCode.ERR;
                }
                if (ans == null) {
                    System.out.println("not found");
                } else {
                    System.out.println("found");
                    System.out.println(mtp.serialize(mp, ans));
                }
                return ExitCode.OK;
            }
            break;
        case "remove":
            if (arg.length == 2) {
                if (mp.getName() == null) {
                    System.out.println("no table");
                    return ExitCode.OK;
                }
                try {
                    ans = mp.remove(arg[1]);
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                    return ExitCode.ERR;
                } 
                if (ans == null) {
                    System.out.println("not found");
                } else {
                    System.out.println("removed");
                }
                return ExitCode.OK;
            }
            break;
        case "create":
            if (arg.length >= 2) {
                try {
                    if (mtp.createTable(arg[1], getTypes(input)) == null) {
                        System.out.println(arg[1] + " exists");
                    } else {
                        System.out.println("created");
                    }
                } catch (IOException e2) {
                    System.err.println(e2.getMessage());
                    return ExitCode.ERR;
                } catch (RuntimeException e1) {
                    System.err.println(e1.getMessage());
                    return ExitCode.ERR;
                }
                return ExitCode.OK;
            }
            break;
        case "drop":
            if (arg.length == 2) {
                try {
                    mtp.removeTable(arg[1]);
                    if (mp.getName() != null && mp.getName().equals(arg[1])) {
                        mp.setNameToNull();
                    }
                    System.out.println("dropped");
                } catch (IllegalStateException e2) {
                    System.out.println(arg[1] + " not exists");
                    System.err.println(e2);
                } catch (RuntimeException e1) {
                    System.err.println(e1.getMessage());
                    return ExitCode.ERR;
                }
                return ExitCode.OK;
            }
            break;
        case "use":
            if (arg.length == 2) {
                if (mp.getUncommitedChangesAndTrack(false) != 0) {
                    System.err.println(mp.getUncommitedChangesAndTrack(false) + " unsaved changes");
                    return ExitCode.ERR;
                }
                try {
                    Filemap newFileMap = (Filemap) mtp.getTable(arg[1]);
                    if (newFileMap == null) {
                        System.out.println(arg[1] + " not exists");
                    } else {
                        mp = newFileMap;
                        System.out.println("using " + arg[1]);
                    }
                } catch (RuntimeException e1) {
                    System.err.println(e1.getMessage());
                    return ExitCode.ERR;
                }
                return ExitCode.OK;
            }
            break;
        case "size":
            if (arg.length == 1) {
                if (mp.getName() == null) {
                    System.out.println("no table");
                    return ExitCode.OK;
                }
                System.out.println(mp.size());
                return ExitCode.OK;
            }
            break;
        case "commit":
            if (arg.length == 1) {
                if (mp.getName() == null) {
                    System.out.println("no table");
                    return ExitCode.OK;
                }
                try {
                    System.out.println(mp.commit());
                } catch (RuntimeException e) {
                    System.err.println(e.getMessage());
                    return ExitCode.ERR;
                }
                return ExitCode.OK;
            }
            break;
        case "rollback":
            if (mp.getName() == null) {
                System.out.println("no table");
                return ExitCode.OK;
            }
            if (arg.length == 1) {
                try {
                    System.out.println(mp.rollback());
                } catch (RuntimeException e) {
                    System.err.println(e.getMessage());
                    return ExitCode.ERR;
                }
                return ExitCode.OK;
            }
            break;
        case "exit":
            if (arg.length == 1) {
                try {
                    mp.commit();
                } catch (RuntimeException e) {
                    System.err.println(e.getMessage());
                    return ExitCode.ERR;
                }
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
