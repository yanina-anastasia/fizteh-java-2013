package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandAbstract;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandShell;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.FileMapUtils.myParsing;

public class FileMapProvider implements CommandAbstract, TableProvider {

    private final Path pathDb;
    private final CommandShell mySystem;
    String useNameTable;
    Set<String> setDirTable;
    FileMap dbData;
    boolean err;
    boolean out;

    public Map<String, Object[]> mapComamnd() {
        Map<String, Object[]> commandList = new HashMap<String, Object[]>(){ {
            put("put",      new Object[] {"multiPut",      true,  2 });
            put("get",      new Object[] {"multiGet",      false, 1 });
            put("remove",   new Object[] {"multiRemove",   false, 1 });
            put("create",   new Object[] {"multiCreate",        false, 1 });
            put("drop",     new Object[] {"multiDrop",          false, 1 });
            put("use",      new Object[] {"multiUse",           false, 1 });
            put("size",     new Object[] {"multiSize",     false, 0 });
            put("commit",   new Object[] {"multiCommit",   false, 0 });
            put("rollback", new Object[] {"multiRollback", false, 0 });
        }};
        return commandList;
    }

    public FileMapProvider(String pathT) throws Exception {
        this.out = true;
        this.err = true;
        File file = null;
        try {
            file = new File(pathT);
        } catch (Exception e) {
            throw new ErrorFileMap(pathT + " папка не открывается");
        }
        if (!file.exists()) {
            throw new ErrorFileMap(pathT + " не существует");
        }
        if (!file.isDirectory()) {
            throw new ErrorFileMap(pathT + " не папка");
        }
        this.useNameTable = "";
        this.pathDb = Paths.get(pathT);
        this.mySystem = new CommandShell(pathT, false, false);
        this.dbData = null;
        this.setDirTable = new HashSet<>();

        try {
            checkBdDir(pathDb);
        } catch (Exception e) {
            e.addSuppressed(new ErrorFileMap("Ошибка загрузки базы"));
            throw e;
        }

    }

    public void exit() throws Exception {
        if (dbData != null) {
            dbData.closeTable();
        }
    }

    private void checkBdDir(Path pathTables) throws Exception {
        File currentFile = pathTables.toFile();
        File[] listFiles = currentFile.listFiles();
        /*if (listFiles == null) {
            throw new ErrorFileMap("папка пуста");
        }
        if (listFiles.length == 0) {
            throw new ErrorFileMap("папка пуста");
        } */
        for (File nameMap : listFiles) {
            if (!nameMap.isDirectory()) {
                throw new ErrorFileMap(nameMap.getAbsolutePath() + " не папка");
            } else {
                setDirTable.add(nameMap.getName());
            }
        }
    }

    private FileMap loadDb(String nameMap) throws Exception {
        try {
            File currentFileMap = pathDb.resolve(nameMap).toFile();
            if (!currentFileMap.isDirectory()) {
                throw new ErrorFileMap(currentFileMap.getAbsolutePath() + " не директория");
            }
            return new FileMap(pathDb, nameMap);
        } catch (Exception e) {
            e.printStackTrace();
            e.addSuppressed(new ErrorFileMap("ошибка открытия таблицы " + nameMap));
            throw e;
            //return null;
        }
    }

    public String startShellString() {
        return "$ ";
    }

    public void multiPut(String[] args) {
        if (useNameTable.equals("")) {
            outPrint("no table");
        } else {
            args = myParsing(args);
            String key = args[0];
            String value = args[1];
            String res = dbData.put(key, value);
            if (res == null) {
                outPrint("new");
            } else {
                outPrint("overwrite");
                outPrint(res);
            }
        }
    }

    public void multiGet(String[] args) {
        if (useNameTable.equals("")) {
            outPrint("no table");
        } else {
            String key = args[0];
            String res = dbData.get(key);
            if (res == null) {
                outPrint("not found");
            } else {
                outPrint("found");
                outPrint(res);
            }
        }
    }

    public void multiRemove(String[] args) {
        if (useNameTable.equals("")) {
            outPrint("no table");
        } else {

            String key = args[0];
            String res = dbData.remove(key);
            if (res == null) {
                outPrint("not found");
            } else {
                outPrint("removed");
            }
        }
    }

    public void multiSize(String[] args) {
        if (useNameTable.equals("")) {
            outPrint("no table");
        } else {
            outPrint(String.valueOf(dbData.size()));
        }
    }

    public void multiCommit(String[] args) {
        outPrint(String.valueOf(dbData.commit()));
    }

    public void multiRollback(String[] args) {
        outPrint(String.valueOf(dbData.rollback()));
    }

    public void multiUse(String[] args) throws Exception {
        int changeKey = 0;
        if (dbData != null) {
            changeKey = dbData.changeKey();
        }

        //if (changeKey > 0) {
        //    errPrint(String.format("%d unsaved changes", changeKey));
        //} else {
            String nameTable = args[0];
            if (!setDirTable.contains(nameTable)) {
                outPrint(nameTable + " not exists");
            } else {
                if (!nameTable.equals(useNameTable)) {
                    useNameTable = nameTable;
                    if (dbData != null) {
                        dbData.closeTable();
                    }
                    dbData = loadDb(nameTable);
                }
                outPrint("using " + nameTable);
            }
        //}
    }

    public void multiDrop(String[] args) throws ErrorFileMap {
        String nameTable = args[0];
        try {
            removeTable(nameTable);
            outPrint("dropped");
        } catch (IllegalStateException e) {
            outPrint(nameTable + " not exists");
        }
    }

    public void multiCreate(String[] args) throws Exception {
        String nameTable = args[0];
        Table fileMap = createTable(nameTable);

        if (fileMap == null) {
            outPrint(nameTable + " exists");
            throw new ErrorFileMap(null);
        } else {
            outPrint("created");
        }
    }

    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (setDirTable.contains(name)) {
            return null;
        } else {
            setDirTable.add(name);
            try {
                FileMap fileMap = new FileMap(pathDb, name);
                return fileMap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (setDirTable.contains(name)) {
            try {
                FileMap fileMap = new FileMap(pathDb, name);
                return fileMap;
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (setDirTable.contains(name)) {
            setDirTable.remove(name);
            try {
                mySystem.rm(new String[]{pathDb.resolve(name).toString()});
            } catch (Exception e) {
                IllegalStateException ex = new IllegalStateException();
                ex.addSuppressed(e);
                throw ex;
            }
        } else {
            throw new IllegalStateException();
        }
    }

    private void errPrint(String message) {
        if (err) {
            System.err.println(message);
        }
    }

    private void outPrint(String message) {
        if (out) {
            System.out.println(message);
        }
    }

}
