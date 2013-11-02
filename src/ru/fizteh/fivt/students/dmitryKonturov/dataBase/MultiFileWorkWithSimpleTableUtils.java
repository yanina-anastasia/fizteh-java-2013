package ru.fizteh.fivt.students.dmitryKonturov.dataBase;


import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellEmulator;
import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellException;

class MultiFileWorkWithSimpleTableUtils {

    private MultiFileWorkWithSimpleTableUtils() {

    }

    private static MultiFileMapShell shell = null;

    static void setShell(MultiFileMapShell newShell) {
        shell = newShell;
    }

    public static ShellEmulator.ShellCommand[] getCommandList() {
        return new ShellEmulator.ShellCommand[] {
                new RemoveShellCommand(),
                new GetShellCommand(),
                new PutShellCommand(),
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

    static class RemoveShellCommand implements ShellEmulator.ShellCommand {
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

            if (shell.currentDatabase == null) {
                System.out.println("no table");
                return;
            }

            String key = realArgs[0];
            String value = (String) shell.currentDatabase.remove(key);
            if (value != null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        }
    }

    static class PutShellCommand implements ShellEmulator.ShellCommand {
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

            if (shell.currentDatabase == null) {
                System.out.println("no table");
                return;
            }

            String key = realArgs[0];
            String value = realArgs[1];

            Object oldValue = shell.currentDatabase.put(key, value);
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

    static class GetShellCommand implements ShellEmulator.ShellCommand {
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

            if (shell.currentDatabase == null) {
                System.out.println("no table");
                return;
            }

            String key = realArgs[0];
            Object value = shell.currentDatabase.get(key);
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
}
