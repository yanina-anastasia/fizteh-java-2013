package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.anastasyev.shell.Command;
import ru.fizteh.fivt.students.anastasyev.shell.State;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.Vector;

public class FileMapTableProvider extends State implements TableProvider {
    private File multiFileHashMapDir;
    private Vector<Command> commands = new Vector<Command>();
    private Hashtable<String, FileMapTable> allFileMapTablesHashtable = new Hashtable<String, FileMapTable>();
    private String currentFileMapTable = null;




    @Override
    public Vector<Command> getCommands() {
        return commands;
    }

    private void isBadName(String val) {
        if (val == null || val.trim().isEmpty()) {
            throw new IllegalArgumentException("tablename " + val + " is null");
        }
        if (val.contains("\\") || val.contains("/") || val.contains(">") || val.contains("<")
                || val.contains("\"") || val.contains(":") || val.contains("?") || val.contains("|")
                || val.startsWith(".") || val.endsWith(".")) {
            throw new RuntimeException("Bad symbols in tablename " + val);
        }
    }

    public FileMapTableProvider(String dbDir) throws IllegalArgumentException, IOException {
        if (dbDir == null || dbDir.trim().isEmpty()) {
            throw new IllegalArgumentException();
        }
        multiFileHashMapDir = new File(dbDir);
        if (!multiFileHashMapDir.exists()) {
            throw new IllegalArgumentException(dbDir + " not exists");
        }
        if (!multiFileHashMapDir.isDirectory()) {
            throw new IllegalArgumentException(dbDir + " is not directory");
        }
        File[] tables = multiFileHashMapDir.listFiles();
        for (File table : tables) {
            if (table.isFile()) {
                continue;
            }
            allFileMapTablesHashtable.put(table.getName(), new FileMapTable(table.toString()));
        }
        commands.add(new PutCommand());
        commands.add(new GetCommand());
        commands.add(new RemoveCommand());
        commands.add(new FileMapExitCommand());
        commands.add(new CreateCommand());
        commands.add(new DropCommand());
        commands.add(new UseCommand());
        commands.add(new RollbackCommand());
        commands.add(new CommitCommand());
        commands.add(new SizeCommand());
    }

    public FileMapTable getCurrentFileMapTable() {
        if (currentFileMapTable == null) {
            return null;
        }
        return allFileMapTablesHashtable.get(currentFileMapTable);
    }

    private void rmTable(Path removing) throws IOException {
        File remove = new File(removing.toString());
        if (!remove.exists()) {
            throw new IOException(removing.getFileName() + " there is not such file or directory");
        }
        if (remove.isFile()) {
            if (!remove.delete()) {
                throw new IOException(removing.getFileName() + " can't remove this file");
            }
        }
        if (remove.isDirectory()) {
            String[] fileList = remove.list();
            for (String files : fileList) {
                rmTable(removing.resolve(files));
            }
            if (!remove.delete()) {
                throw new IOException(removing.getFileName() + " can't remove this directory");
            }
        }
    }

    public Table getTable(String name) throws IllegalArgumentException, RuntimeException {
        isBadName(name);
        return allFileMapTablesHashtable.get(name);
    }

    @Override
    public Table createTable(String name) throws IllegalArgumentException, RuntimeException {
        isBadName(name);
        if (allFileMapTablesHashtable.containsKey(name)) {
            return null;
        }
        File newFileMapTable = new File(multiFileHashMapDir.toString() + File.separator + name);
        FileMapTable fileMapTable = null;
        try {
            fileMapTable = new FileMapTable(newFileMapTable.toString());
            allFileMapTablesHashtable.put(name, fileMapTable);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return fileMapTable;
    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        isBadName(name);
        FileMapTable deleteTable = allFileMapTablesHashtable.get(name);
        if (deleteTable == null) {
            throw new IllegalStateException("TableName is null");
        }
        try {
            rmTable(multiFileHashMapDir.toPath().resolve(name));
            allFileMapTablesHashtable.remove(name);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        if (deleteTable.equals(currentFileMapTable)) {
            currentFileMapTable = null;
        }
    }

    public void setCurrentTable(String name) throws IOException {
        isBadName(name);
        if (!allFileMapTablesHashtable.containsKey(name)) {
            System.out.println(name + " not exists");
            return;
        }
        if (currentFileMapTable != null) {
            int uncommitedSize = allFileMapTablesHashtable.get(currentFileMapTable).uncommittedSize();
            if (uncommitedSize != 0) {
                System.out.println(uncommitedSize + " unsaved changes");
                return;
            }
        }
        currentFileMapTable = name;
        System.out.println("using " + name);
    }
}
