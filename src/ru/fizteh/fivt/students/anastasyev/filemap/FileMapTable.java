package ru.fizteh.fivt.students.anastasyev.filemap;

import ru.fizteh.fivt.students.anastasyev.shell.Command;
import ru.fizteh.fivt.students.anastasyev.shell.State;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.Vector;

public class FileMapTable extends State {
    private File multiFileHashMapDir;
    private File currentFileMapTable = null;
    private FileMap[][] mapsTable;
    private Vector<Command> commands = new Vector<Command>();
    private Hashtable<String, File> allFileMapTablesHashtable = new Hashtable<String, File>();

    @Override
    public Vector<Command> getCommands() {
        return commands;
    }

    public FileMapTable(String dbDir) {
        multiFileHashMapDir = new File(dbDir);
        if (!multiFileHashMapDir.exists()) {
            System.err.println(dbDir + " not exists");
            System.exit(1);
        }
        File[] tables = multiFileHashMapDir.listFiles();
        for (File table : tables) {
            allFileMapTablesHashtable.put(table.getName(), table);
        }
        commands.add(new PutCommand());
        commands.add(new GetCommand());
        commands.add(new RemoveCommand());
        commands.add(new FileMapExitCommand());
        commands.add(new CreateCommand());
        commands.add(new DropCommand());
        commands.add(new UseCommand());
    }

    public void createTable(String tableName) throws IOException {
        if (allFileMapTablesHashtable.containsKey(tableName)) {
            System.out.println(tableName + " exists");
        } else {
            File newFileMapTable = new File(multiFileHashMapDir.toString() + File.separator + tableName);
            if (!newFileMapTable.exists()) {
                if (!newFileMapTable.mkdir()) {
                    throw new IOException("Can't create " + tableName);
                }
            }
            if (!newFileMapTable.isDirectory()) {
                throw new IOException(newFileMapTable.getName() + " is not a directory");
            }
            allFileMapTablesHashtable.put(tableName, newFileMapTable);
            System.out.println("created");
        }
    }

    private void remove(Path removing) throws IOException {
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
                remove(removing.resolve(files));
            }
            if (!remove.delete()) {
                throw new IOException(removing.getFileName() + " can't remove this directory");
            }
        }
    }

    public void dropTable(String tableName) throws IOException {
        File deleteTable = allFileMapTablesHashtable.get(tableName);
        if (deleteTable == null) {
            System.out.println(tableName + " not exists");
            return;
        }
        if (deleteTable == currentFileMapTable) {
            currentFileMapTable = null;
            mapsTable = new FileMap[16][16];
        }
        try {
            remove(deleteTable.toPath());
            allFileMapTablesHashtable.remove(tableName);
            System.out.println("dropped");
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void useTable(String tableName) throws IOException {
        if (currentFileMapTable != null) {
            this.save();
        }
        if (!allFileMapTablesHashtable.containsKey(tableName)) {
            System.out.println(tableName + " not exists");
            return;
        }
        currentFileMapTable = allFileMapTablesHashtable.get(tableName);
        mapsTable = new FileMap[16][16];
        for (int i = 0; i < 16; ++i) {
            File dbDir = new File(currentFileMapTable.toString() + File.separator + i + ".dir");
            if (dbDir.exists()) {
                if (!dbDir.isDirectory()) {
                    throw new IOException(i + ".dir is not table subdirectory");
                }
                for (int j = 0; j < 16; ++j) {
                    File dbDat = new File(dbDir.toString() + File.separator + j + ".dat");
                    if (dbDat.exists()) {
                        if (!dbDat.isFile()) {
                            throw new IOException(i + ".dat is not a FileMap file");
                        }
                        mapsTable[i][j] = new FileMap(dbDat.toString(), i, j);
                        if (mapsTable[i][j].isEmpty()) {
                            mapsTable[i][j].delete();
                            mapsTable[i][j] = null;
                        }
                    }
                }
            }
        }
        System.out.println("using " + tableName);
    }

    public void deleteFileMap(int hashCode) throws IOException {
        mapsTable[hashCode % 16][hashCode / 16 % 16].delete();
        mapsTable[hashCode % 16][hashCode / 16 % 16] = null;
    }

    @Override
    public void save() throws IOException {
        if (currentFileMapTable == null) {
            return;
        }
        for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (mapsTable[i][j] != null) {
                        mapsTable[i][j].save();
                    }
            }
        }
    }

    @Override
    public FileMap getMyState(int hashCode) throws IOException {
        if (currentFileMapTable == null) {
            throw new IOException("no table");
        }
        if (hashCode < 0) {
            throw new IOException("Invalid hash");
        }
        return mapsTable[hashCode % 16][hashCode / 16 % 16];
    }

    public FileMap openFileMap(int hashCode) throws IOException {
        File dir = new File(currentFileMapTable.toString() + File.separator + hashCode % 16 + ".dir");
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new IOException("Can't create " + hashCode % 16 + ".dir");
            }
        }
        String datName = dir.toString() + File.separator + hashCode / 16 % 16 + ".dat";
        if (hashCode >= 0 && mapsTable[hashCode % 16][hashCode / 16 % 16] == null) {
            mapsTable[hashCode % 16][hashCode / 16 % 16] = new FileMap(datName);
        }
        return mapsTable[hashCode % 16][hashCode / 16 % 16];
    }
}
