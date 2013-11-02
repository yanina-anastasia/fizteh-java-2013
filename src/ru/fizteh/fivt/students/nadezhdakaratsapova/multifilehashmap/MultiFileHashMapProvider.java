package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.nadezhdakaratsapova.filemap.DataTable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.filemap.FileWriter;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.CommandUtils;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultiFileHashMapProvider implements TableProvider {
    public static final int DIR_COUNT = 16;
    public static final int FILE_COUNT = 16;
    public static final String TABLE_NAME = "[a-zA-Zа-яА-Я0-9]+";
    private File workingDirectory;
    public DataTable curDataBaseStorage = null;
    private Map<String, DataTable> dataBaseTables = new HashMap<String, DataTable>();


    public MultiFileHashMapProvider(File dir) throws IOException {
        workingDirectory = dir;
        File[] tables = workingDirectory.listFiles();
        if (tables.length != 0) {
            for (File f : tables) {
                if (f.isDirectory()) {
                    DataTable dataTable = new DataTable(f.getName(), workingDirectory);
                    dataTable.load();
                    dataBaseTables.put(f.getName(), dataTable);
                }
            }
        }
    }


    public DataTable setCurTable(String newTable) {
        DataTable dataTable = null;
        if (!dataBaseTables.isEmpty()) {
            dataTable = dataBaseTables.get(newTable);
            if (dataTable != null) {
                curDataBaseStorage = dataTable;
            }
        }
        return dataTable;
    }

    public Table getTable(String name) throws IllegalArgumentException {
        if ((name == null) || (name.isEmpty())) {
            throw new IllegalArgumentException("The table has not allowed name");
        }
        if (!name.matches(TABLE_NAME)) {
            throw new RuntimeException("Not correct file name");
        }
        return dataBaseTables.get(name);
    }

    public Table createTable(String name) throws IllegalArgumentException {
        if ((name == null) || (name.isEmpty())) {
            throw new IllegalArgumentException("The table has not allowed name");
        }
        if (!name.matches(TABLE_NAME)) {
            throw new RuntimeException("Not correct file name");
        }
        if (dataBaseTables.get(name) != null) {
            return null;
        } else {
            File newTableFile = new File(workingDirectory, name);
            try {
                newTableFile = newTableFile.getCanonicalFile();
            } catch (IOException e) {
                throw new IllegalArgumentException("Programme's mistake in getting canonical file");
            }
            newTableFile.mkdir();
            DataTable newTable = new DataTable(name, workingDirectory);
            dataBaseTables.put(name, newTable);
            return newTable;
        }
    }

    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if ((name == null) || (name.isEmpty())) {
            throw new IllegalArgumentException("The table has not allowed name");
        }
        if (!name.matches(TABLE_NAME)) {
            throw new RuntimeException("Not correct file name");
        }
        if (dataBaseTables.get(name) != null) {
            File table = new File(workingDirectory, name);
            try {
                table = table.getCanonicalFile();
            } catch (IOException e) {
                throw new IllegalArgumentException("Programme's mistake in getting canonical file");
            }
            try {
                CommandUtils.recDeletion(table);
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
            dataBaseTables.remove(name);

        } else {
            throw new IllegalStateException(name + "not exists");
        }
    }

    public void writeData() throws IOException {
        if (!dataBaseTables.isEmpty()) {
            Set<String> tablesNames = dataBaseTables.keySet();

            for (String tableName : tablesNames) {
                DataTable table = dataBaseTables.get(tableName);
                Set<String> keys = table.getKeys();
                if (!keys.isEmpty()) {
                    for (int i = 0; i < DIR_COUNT; ++i) {
                        File dir = new File(new File(table.getWorkingDirectory(), tableName), new String(i + ".dir"));
                        for (int j = 0; j < FILE_COUNT; ++j) {
                            DataTable keysToFile = new DataTable();
                            File file = new File(dir, new String(j + ".dat"));
                            for (String key : keys) {
                                int hashByte = Math.abs(key.getBytes()[0]);
                                int ndirectory = hashByte % DIR_COUNT;
                                int nfile = (hashByte / DIR_COUNT) % FILE_COUNT;
                                if ((ndirectory == i) && (nfile == j)) {
                                    if (!dir.getCanonicalFile().exists()) {
                                        dir.getCanonicalFile().mkdir();
                                    }

                                    if (!file.getCanonicalFile().exists()) {
                                        file.getCanonicalFile().createNewFile();
                                    }
                                    keysToFile.put(key, table.get(key));
                                    keysToFile.commit();
                                }
                            }

                            if (!keysToFile.isEmpty()) {
                                FileWriter fileWriter = new FileWriter();
                                fileWriter.writeDataToFile(file.getCanonicalFile(), keysToFile);
                            } else {
                                if (file.getCanonicalFile().exists()) {
                                    file.getCanonicalFile().delete();
                                }
                            }
                        }
                        if (dir.getCanonicalFile().listFiles() == null) {
                            dir.delete();
                        }
                    }
                }
            }
        }
    }

}


