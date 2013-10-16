package ru.fizteh.fivt.students.dmitryIvanovsky.multiFileHashMap;

import ru.fizteh.fivt.students.dmitryIvanovsky.fileMap.MyFileMap;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher.Code;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandShell;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class MyMultiFileMap extends MyFileMap {

    String useNameTable;
    MyFileMap useTable;
    Path pathTables;
    CommandShell mySystem;
    Set<String> setEmptyTable = new HashSet<>();

    public void exit() throws IOException{
        if (!useNameTable.equals("")) {
            useTable.closeDbFile();
        }
        for (String table : setEmptyTable) {
            mySystem.rm(new String[]{table});
        }
    }

    public MyMultiFileMap(String pathT) throws IOException {
        super();
        //super(dbFile, pathDbFile);

        File file = new File(pathT);
        if (!file.exists() || !file.isDirectory()) {
            throw new IOException();
        }
        useNameTable = "";
        pathTables = Paths.get(pathT);
        mySystem = new CommandShell(pathT);
    }

    public Code multiGet(String[] args) {
        if (useNameTable.equals("")) {
            System.out.println("no table");
            return Code.OK;
        } else {
            return useTable.get(args);
        }
    }

    public Code multiPut(String[] args) {
        if (useNameTable.equals("")) {
            System.out.println("no table");
            return Code.OK;
        } else {
            if (useTable.put(args) != Code.ERROR) {
                setEmptyTable.remove(useNameTable);
                return Code.OK;
            } else {
                return Code.ERROR;
            }
        }
    }

    public Code multiRemove(String[] args) {
        if (useNameTable.equals("")) {
            System.out.println("no table");
            return Code.OK;
        } else {
            if (useTable.remove(args) != Code.ERROR) {
                if (useTable.isEmpty()) {
                    setEmptyTable.add(useNameTable);
                }
                return Code.OK;
            } else {
                return Code.ERROR;
            }
        }
    }

    public Code use(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("У команды use 1 аргумент");
            return Code.ERROR;
        } else {
            String nameTable = args[0];

            //System.out.println(mySystem.getCurrentFile());

            if (mySystem.cd(args) == Code.ERROR) {
                mySystem.cd(new String[]{pathTables.toString()});
                System.out.println("tablename not exists");
                return Code.ERROR;
            } else {
                mySystem.cd(new String[]{pathTables.toString()});

                if (!nameTable.equals(useNameTable)) {
                    if (!useNameTable.equals("")) {
                        useTable.closeDbFile();
                        if (useTable.isEmpty()) {
                            mySystem.rm(new String[]{useNameTable});
                        }
                    }
                    useNameTable = nameTable;
                    useTable = new MyFileMap(pathTables.resolve(useNameTable).toString());
                }

                System.out.println("using tablename");
                return Code.OK;
            }
        }
    }

    public Code drop(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("У команды drop 1 аргумент");
            return Code.ERROR;
        } else {
            String dropTable = args[0];
            if (dropTable.equals(useNameTable)) {
                useTable.closeDbFile();
                useNameTable = "";
            }
            if (mySystem.rm(args) == Code.ERROR) {
                System.out.println("tablename not exists");
                return Code.ERROR;
            } else {
                setEmptyTable.remove(args[0]);
                System.out.println("dropped");
                return Code.OK;
            }
        }
    }

    public Code create(String[] args) {
        if (args.length != 1) {
            System.err.println("У команды create 1 аргумент");
            return Code.ERROR;
        } else {
            Code res = mySystem.mkdir(args);
            if (res == Code.ERROR) {
                System.out.println("tablename exists");
                return Code.ERROR;
            } else {
                setEmptyTable.add(args[0]);
                System.out.println("created");
                return Code.OK;
            }
        }
    }
}
