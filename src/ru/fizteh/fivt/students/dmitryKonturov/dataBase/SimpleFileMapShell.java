package ru.fizteh.fivt.students.dmitryKonturov.dataBase;

import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellEmulator;
import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellException;
import java.nio.file.Path;

/**
 *  Простой шелл, работает с одной базой.
 *  Shell allows to work with database.
 *  Operations with database:
 *      put key value
 *      get key
 *      remove key
 *      exit
 */

public class SimpleFileMapShell extends ShellEmulator {
    protected Path databasePath;
    protected SimpleDatabase currentDataBase;

    @Override
    protected String[] shellParseArguments(String bigArg) {
        String newBigArg = bigArg.trim();
        String[] args;
        if (newBigArg.length() == 0) {
            args = new String[0];
        } else {
            args = new String[1];
            args[0] = newBigArg;
        }
        return args;
    }

    @Override
    protected void justBeforeExecutingAction(String commandName) {
        SimpleFileMapShellUtils.setShell(this);
    }

    @Override
    public void packageMode(String query) throws ShellException {
        super.packageMode(query);
        try {
            SimpleDatabaseLoaderWriter.databaseWriteToFile(currentDataBase, databasePath);
        } catch (DatabaseException e) {
            System.err.println(e.toString());
            System.exit(1);
        }
    }

    @Override
    public void interactiveMode() {
        super.interactiveMode();
        try {
            SimpleDatabaseLoaderWriter.databaseWriteToFile(currentDataBase, databasePath);
        } catch (DatabaseException e) {
            System.err.println(e.toString());
            System.exit(1);
        }
    }

    SimpleFileMapShell(Path databasePath) {
        currentDataBase = new SimpleDatabase();
        this.databasePath = databasePath;
        try {
            SimpleDatabaseLoaderWriter.databaseLoadFromFile(currentDataBase, databasePath);
        } catch (DatabaseException e) {
            System.err.println(e.toString());
            System.exit(1);
        }
        addToCommandList(SimpleFileMapShellUtils.getCommandList());
    }

    SimpleFileMapShell() {
        currentDataBase = null;
        databasePath = null;
    }
}
