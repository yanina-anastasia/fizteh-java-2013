package ru.fizteh.fivt.students.dmitryKonturov.dataBase;


import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellEmulator;
import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellException;

import java.nio.file.Path;

class MultiFileMapShell extends ShellEmulator {

    final Path dataBasesWorkspace;
    protected String currentDatabaseName;
    protected SimpleDatabase currentDatabase;

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
        MultiFileDatabaseUtils.setShell(this);
        MultiFileSimpleCommands.setShell(this);
    }

    @Override
    public void packageMode(String query) throws ShellException {
        super.packageMode(query);
        try {
            if (currentDatabase != null && currentDatabaseName != null) {
                MultiFileMapLoaderWriter.writeDatabase(dataBasesWorkspace, currentDatabaseName, currentDatabase);
            }
        } catch (Exception e) {
            throw new ShellException(e.toString(), null);
        }
    }

    @Override
    public void interactiveMode() {
        super.interactiveMode();
        try {
            if (currentDatabase != null && currentDatabaseName != null) {
                MultiFileMapLoaderWriter.writeDatabase(dataBasesWorkspace, currentDatabaseName, currentDatabase);
            }
        } catch (Exception e) {
            System.err.println(e.toString());
            System.exit(1);
        }
    }


    MultiFileMapShell(Path workingDir) throws DatabaseException {
        CheckDatabasesWorkspace.checkWorkspace(workingDir);
        dataBasesWorkspace = workingDir;
        currentDatabase = null;
        currentDatabaseName = null;
        addToCommandList(MultiFileDatabaseUtils.getCommandsList());
        addToCommandList(MultiFileSimpleCommands.getCommandList());
    }
}
