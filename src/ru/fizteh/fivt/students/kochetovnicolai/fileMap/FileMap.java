package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.students.kochetovnicolai.shell.Launcher;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class FileMap {

    private HashMap<String, Executable> commands;

    private FileMap(TableManager manager) {
        commands = new HashMap<>();
        commands.put("exit", new TableCommandExit(manager));
        commands.put("get", new TableCommandGet(manager));
        commands.put("put", new TableCommandPut(manager));
        commands.put("remove", new TableCommandRemove(manager));
    }

    public static void main(String[] args) {
        File tableDirectory = new File(System.getProperty("fizteh.db.dir"));
        TableManager manager = null;
        try {
            manager = new TableManager(tableDirectory, "db.dat");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        FileMap fileMap = new FileMap(manager);
        Launcher launcher = new Launcher(fileMap.commands);
        try {
            if (!launcher.launch(args, manager)) {
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
