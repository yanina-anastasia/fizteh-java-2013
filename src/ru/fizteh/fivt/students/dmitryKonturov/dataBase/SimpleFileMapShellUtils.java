package ru.fizteh.fivt.students.dmitryKonturov.dataBase;

import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellEmulator.ShellCommand;
import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellException;

/**
 *  Interface between database and shell
 */

public class SimpleFileMapShellUtils {

    private SimpleFileMapShellUtils() {

    }

    private static SimpleFileMapShell shell = null;

    static void setShell(SimpleFileMapShell newShell) {
        shell = newShell;
    }

    public static ShellCommand[] getCommandList() {
        return new ShellCommand[] {
                new RemoveShellCommand(),
                new GetShellCommand(),
                new PutShellCommand(),
                new ExitShellCommand()
        };
    }

    private static String[] getKeyValueArgPair(String bigArg) throws ShellException {
        String[] tmpStr = bigArg.split("\\s", 2);
        if (tmpStr.length >= 2) {
            tmpStr[1] = tmpStr[1].trim();
        }
        if (tmpStr.length > 2) {
            throw new ShellException("", "Bad arguments");
        }
        return tmpStr;
    }

    static class RemoveShellCommand implements ShellCommand {
        @Override
        public String getName() {
            return "remove";
        }

        @Override
        public void execute(String[] args) throws ShellException {
            if (args.length != 1) {
                throw new ShellException(getName(), "Bad arguments");
            }

            String[] realArgs;
            realArgs = getKeyValueArgPair(args[0]);

            if (realArgs.length != 1) {
                throw new ShellException(getName(), "Bad arguments");
            }

            if (shell == null) {
                throw new ShellException(getName(), "Bad shell");
            }

            if (shell.currentDataBase == null) {
                System.out.println("no table");
                throw new ShellException(getName(), "no table");
            }

            String key = realArgs[0];
            String value = (String) shell.currentDataBase.remove(key);
            if (value != null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        }
    }

    static class PutShellCommand implements ShellCommand {
        @Override
        public String getName() {
            return "put";
        }

        @Override
        public void execute(String[] args) throws ShellException {
            if (args.length != 1) {
                throw new ShellException(getName(), "Bad arguments");
            }

            String[] realArgs;
            realArgs = getKeyValueArgPair(args[0]);

            if (realArgs.length != 2) {
                throw new ShellException(getName(), "Bad arguments");
            }

            if (shell == null) {
                throw new ShellException(getName(), "Bad shell");
            }

            if (shell.currentDataBase == null) {
                System.out.println("no table");
                throw new ShellException(getName(), "no table");
            }

            String key = realArgs[0];
            String value = realArgs[1];

            Object oldValue = shell.currentDataBase.put(key, value);
            if (oldValue == null) {
                System.out.println("new");
            } else if (oldValue instanceof String) {
                System.out.println("overwrite");
                System.out.println((String) oldValue);
            } else {
                System.err.println("Bad Database: Not only strings");
                System.exit(1);
            }

        }
    }

    static class GetShellCommand implements ShellCommand {
        @Override
        public String getName() {
            return "get";
        }

        @Override
        public void execute(String[] args) throws ShellException {
            if (args.length != 1) {
                throw new ShellException(getName(), "Bad arguments");
            }

            String[] realArgs;
            realArgs = getKeyValueArgPair(args[0]);

            if (realArgs.length != 1) {
                throw new ShellException(getName(), "Bad arguments");
            }

            if (shell == null) {
                throw new ShellException(getName(), "Bad shell");
            }

            if (shell.currentDataBase == null) {
                System.out.println("no table");
                throw new ShellException(getName(), "no table");
            }

            String key = realArgs[0];
            Object value = shell.currentDataBase.get(key);
            if (value == null) {
                System.out.println("not found");
            } else if (value instanceof String) {
                System.out.println("found");
                System.out.println((String) value);
            } else {
                System.err.println("Bad Database: Not only strings");
                System.exit(1);
            }
        }
    }

    static class ExitShellCommand implements ShellCommand {
        @Override
        public String getName() {
            return "exit";
        }

        @Override
        public void execute(String[] args) throws ShellException {

            if (args.length != 0) {
                throw new ShellException(getName(), "Bad arguments");
            }

            if (shell == null) {
                throw new ShellException(getName(), "Bad shell");
            }

            try {
                if (shell.currentDataBase != null && shell.databasePath != null) {
                    SimpleDatabaseLoaderWriter.databaseWriteToFile(shell.currentDataBase, shell.databasePath);
                }
            } catch (DatabaseException e) {
                System.err.println(e.toString());
                System.exit(1);
            }
            System.exit(0);
        }
    }
}
