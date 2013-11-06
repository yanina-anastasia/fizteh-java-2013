package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class NewTableProvider implements TableProvider {
    private File workingDirectory;
    private NewTable currentTable = null;
    private HashMap<String, NewTable> tables = new HashMap<>();

    public NewTableProvider(File dir) {
        workingDirectory = dir;
        for (File file : workingDirectory.listFiles()) {
            if (file.isDirectory()) {
                tables.put(file.getName(), new NewTable(file.getName(), this));
            }
        }
    }

    public NewTable getNewCurrentTable() {
        return currentTable;
    }

    public File getCurrentTableFile() {
        if (currentTable == null) {
            return null;
        }
        return new File(workingDirectory, currentTable.getName());
    }

    public void setCurrentTable(NewTable table) {
        currentTable = table;
    }

    private boolean checkNameOfDataBaseDirectory(String dir) {
        return dir.matches("(([0-9])|(1[0-5]))\\.dir");
    }

    private boolean checkNameOfFiles(String file) {
        return file.matches("(([0-9])|(1[0-5]))\\.dat");
    }

    private File getFile(String key) {
        byte c = 0;
        c = (byte) Math.abs(key.getBytes(StandardCharsets.UTF_8)[0]);
        int ndirectory = c % 16;
        int nfile = c / 16 % 16;
        File fileDir = new File(workingDirectory + File.separator + currentTable.getName() + File.separator
                + ndirectory + ".dir");
        if (!fileDir.exists()) {
            if (!fileDir.mkdir()) {
                throw new RuntimeException("Can't create file");
            }
        }
        File file = new File(fileDir, nfile + ".dat");
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new RuntimeException("Can't create file");
                }
            } catch (IOException e) {
                throw new RuntimeException("Can't create file");
            }
        }
        return file;
    }

    @Override
    public Table getTable(String name) {
        checkTableName(name);
        NewTable table = tables.get(name);
        File tableFile = new File(workingDirectory, name);
        try {
            if (table != null) {
                table.loadCommittedValues(load(tableFile));
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return table;
    }

    private HashMap<String, String> load(File tableFile) throws IOException {
        HashMap<String, String> map = new HashMap<String, String>();
        for (File dir : tableFile.listFiles()) {
            if (checkNameOfDataBaseDirectory(dir.getName()) && dir.isDirectory()) {
                for (File file : dir.listFiles()) {
                    if (checkNameOfFiles(file.getName()) && file.isFile()) {
                        if (file.length() != 0) {
                            map.putAll(ReadDataBase.loadFile(file));
                        }
                    }
                }
            }
        }
        return map;
    }

    private void checkTableName(String name) {
        if ((name == null) || (name.trim().isEmpty()) || (!name.matches("[a-zA-Z0-9а-яА-Я]+"))) {
            throw new IllegalArgumentException("Incorrect table name");
        }
    }

    @Override
    public Table createTable(String name) {
        checkTableName(name);
        if (tables.get(name) != null) {
            return null;
        } else {
            File table = new File(workingDirectory, name);
            table.mkdir();
            tables.put(name, new NewTable(name, this));
            return tables.get(name);
        }
    }

    public void saveChanges(File tableFile) throws IOException {
        NewTable table = tables.get(tableFile.getName());
        HashMap<File, HashMap<String, String>> files = makeFiles(tableFile);
        removeTable(table.getName());
        for (File file : files.keySet()) {
            WriteInDataBase.saveFile(file, files.get(file));
        }

    }

    private HashMap<File, HashMap<String, String>> makeFiles(File tableFile) {
        HashMap<File, HashMap<String, String>> files = new HashMap<File, HashMap<String, String>>();
        NewTable table = tables.get(tableFile.getName());
        HashMap<String, String> map = table.returnMap();
        for (String key : map.keySet()) {
            File file = getFile(key);
            if (!files.containsKey(file)) {
                files.put(file, new HashMap<String, String>());
            }
            files.get(file).put(key, map.get(key));
        }
        return files;
    }

    @Override
    public void removeTable(String name) {
        checkTableName(name);
        NewTable table = tables.remove(name);
        File tableFile = new File(workingDirectory, name);
        if (table == null) {
            throw new IllegalStateException("Table does not exist");
        } else {
            for (File dir : tableFile.listFiles()) {
                if (checkNameOfDataBaseDirectory(dir.toString()) && dir.isDirectory()) {
                    for (File file : dir.listFiles()) {
                        if (checkNameOfFiles(file.getName()) && file.isFile()) {
                            file.delete();
                        }
                    }
                }
            }
            tableFile.delete();
        }
    }
}
