package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.ryabovaMaria.shell.ShellCommands;

public class TableProviderCommands implements TableProvider {
    private File curDir;
    private File tableDir;
    public Table myTable;
    
    TableProviderCommands(File tablesDir) {
        curDir = tablesDir;
    }
    
    private void isCorrectArgument(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (name.contains("/")) {
            throw new IllegalArgumentException("argument contains illegal symbols");
        }
        tableDir = curDir.toPath().resolve(name).normalize().toFile();
    }
    
    @Override
    public Table getTable(String name) {
        isCorrectArgument(name);
        if (!tableDir.exists()) {
            throw new IllegalArgumentException(name + " doesn't exists");
        }
        if (!tableDir.isDirectory()) {
            throw new IllegalArgumentException(name + " is not a directory");
        }
        myTable = new TableCommands(tableDir);
        return myTable;
    }

    @Override
    public Table createTable(String name) {
        isCorrectArgument(name);
        if (tableDir.exists()) {
            return null;
        }
        if (!tableDir.mkdir()) {
            throw new IllegalArgumentException(name + " cannot be created");
        } else {
            myTable = new TableCommands(tableDir);
            return myTable;
        }
    }

    @Override
    public void removeTable(String name) {
        isCorrectArgument(name);
        if (!tableDir.isDirectory()) {
            throw new IllegalStateException(name + " cannot be deleted");
        }
        ShellCommands shellCommands = new ShellCommands();
        shellCommands.lexems = new String[2];
        shellCommands.lexems[1] = name;
        shellCommands.currentDir = curDir;
        try {
            shellCommands.rm();
        } catch (Exception e) {
            throw new IllegalStateException(name + " cannot be deleted");
        }
    }
}
