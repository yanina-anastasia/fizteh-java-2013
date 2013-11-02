package ru.fizteh.fivt.students.dmitryKonturov.dataBase;

import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellEmulator.ShellCommand;
import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class MultiFileTablesManagerUtils {

    private MultiFileTablesManagerUtils() {

    }

    private static MultiFileMapShell shell;

    static void setShell(MultiFileMapShell newShell) {
        shell = newShell;
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

    static ShellCommand[] getCommandsList() {
        return new ShellCommand[] {
                new CreateShellCommand(),
                new DropShellCommand(),
                new ExitShellCommand(),
                new UseShellCommand()
        };
    }

    static class CreateShellCommand implements ShellCommand {
        @Override
        public String getName() {
            return "create";
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

            String toCreate = realArgs[0];
            try {
                Path pathToCreate = shell.dataBasesWorkspace.resolve(toCreate);
                if (Files.isDirectory(pathToCreate)) {
                    System.out.println(toCreate + " exists");
                } else {
                    Files.createDirectory(pathToCreate);
                    System.out.println("created");
                }
            } catch (IOException e) {
                throw new ShellException("create: Couldnt create directory", e);
            }

        }
    }

    static void recursiveRemove(Path toRemove) throws ShellException {
        try {
            File file = toRemove.toFile();
            File[] entries = file.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    recursiveRemove(entry.toPath());
                }
            }
            if (!file.delete()) {
                throw new ShellException(file.toString(), "Cannot delete file");
            }
        } catch (ShellException e) {
            throw new ShellException("Cannot remove", toRemove.toAbsolutePath().toString());
        } catch (Exception e) {
            throw new ShellException("Cannot remove", e);
        }
    }

    static class DropShellCommand implements ShellCommand {
        @Override
        public String getName() {
            return "drop";
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

            try {
                String toDropDatabaseName = realArgs[0];
                if (shell.currentDatabaseName != null) {
                    if (shell.currentDatabaseName.equals(toDropDatabaseName)) {
                        shell.currentDatabaseName = null;
                        shell.currentDatabase = null;
                    }
                }
                Path toRemove = shell.dataBasesWorkspace.resolve(toDropDatabaseName);
                if (Files.isDirectory(toRemove)) {
                    recursiveRemove(toRemove);
                    System.out.println("dropped");
                } else {
                    System.out.println(toDropDatabaseName + " not exists");
                }
            } catch (Exception e) {
                throw new ShellException("Cannot drop", e);
            }
        }
    }

    static class UseShellCommand implements ShellCommand {
        @Override
        public String getName() {
            return "use";
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

            String databaseName = realArgs[0];

            try {
              if (shell.currentDatabase != null && shell.currentDatabaseName != null) {
                  MultiFileMapLoaderWriter.writeDatabase(shell.dataBasesWorkspace,
                                                         shell.currentDatabaseName,
                                                         shell.currentDatabase);
              }
                //shell.currentDatabase = null;
                //shell.currentDatabaseName = null;
            } catch (Exception e) {
                throw new ShellException("write", e);
            }

            try {
                Path databaseToUse = shell.dataBasesWorkspace.resolve(databaseName);
                if (!Files.isDirectory(databaseToUse)) {  // CheckDir?
                    System.out.println(databaseName + " not exists");
                } else {
                    shell.currentDatabaseName = databaseName;
                    shell.currentDatabase = new SimpleDatabase();
                    MultiFileMapLoaderWriter.loadDatabase(shell.dataBasesWorkspace,
                                                          shell.currentDatabaseName,
                                                          shell.currentDatabase);
                    System.out.println("using " + databaseName);

                }
            } catch (Exception e) {
                shell.currentDatabaseName = null;
                shell.currentDatabase = null;
                throw new ShellException(getName(), e);
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
                System.err.println("Bad shell");
                System.exit(1);
            }

            try {
                if (shell.currentDatabase != null && shell.currentDatabaseName != null) {
                    MultiFileMapLoaderWriter.writeDatabase(shell.dataBasesWorkspace,
                                                           shell.currentDatabaseName,
                                                           shell.currentDatabase);
                }
                System.exit(0);
            } catch (Exception e) {
                System.err.println(e.toString());
                System.exit(1);
            }
        }

    }

}
