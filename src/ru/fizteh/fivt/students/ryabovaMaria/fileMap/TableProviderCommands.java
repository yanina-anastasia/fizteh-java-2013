package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import java.util.HashMap;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.ryabovaMaria.shell.ShellCommands;

public class TableProviderCommands implements TableProvider {
    private File curDir;
    private File tableDir;
    public Table myTable;
    private HashMap<String, Table> names;
    
    TableProviderCommands(File tablesDir) {
        curDir = tablesDir;
        names = new HashMap<String, Table>();
    }
    
    private void isCorrectArgument(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (name.isEmpty() || name.contains("/") || name.contains("\\") || name.contains(".")) {
            throw new IllegalArgumentException("argument contains illegal symbols");
        }
        tableDir = curDir.toPath().resolve(name).normalize().toFile();
    }
    
    @Override
    public Table getTable(String name) {
        isCorrectArgument(name);
        if (!tableDir.exists()) {
            return null;
        }
        if (!tableDir.isDirectory()) {
            throw new IllegalArgumentException(name + " is not a directory");
        }
        myTable = names.get(name);
        if (myTable == null) {
            myTable = new TableCommands(tableDir);
            names.put(name, myTable);
        }
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
            names.put(name, myTable);
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
            names.remove(name);
        } catch (Exception e) {
            throw new IllegalStateException(name + " cannot be deleted");
        }
    }
}
