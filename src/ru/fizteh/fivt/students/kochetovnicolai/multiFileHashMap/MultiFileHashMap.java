package ru.fizteh.fivt.students.kochetovnicolai.multiFileHashMap;

import ru.fizteh.fivt.students.kochetovnicolai.fileMap.*;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Executable;
import ru.fizteh.fivt.students.kochetovnicolai.shell.Launcher;
import ru.fizteh.fivt.students.kochetovnicolai.shell.StringParser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MultiFileHashMap {

    private HashMap<String, Executable> commands;

    private MultiFileHashMap(DistributedTableManager manager) {
        commands = new HashMap<>();
        commands.put("exit", new TableCommandExit(manager));
        commands.put("get", new TableCommandGet(manager));
        commands.put("put", new TableCommandPut(manager));
        commands.put("remove", new TableCommandRemove(manager));
        commands.put("create", new DistributedTableCommandCreate(manager));
        commands.put("drop", new DistributedTableCommandDrop(manager));
        commands.put("use", new DistributedTableCommandUse(manager));
    }

    public static void main(String[] args) {
        File tableDirectory;
        String property = System.getProperty("fizteh.db.dir");
        if (property == null) {
            System.err.println("property fizteh.db.dir not found");
            System.exit(1);
        }
        tableDirectory = new File(property);
        DistributedTableManager manager = null;
        try {
            manager = new DistributedTableManager(tableDirectory);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        MultiFileHashMap fileHashMap = new MultiFileHashMap(manager);
        Launcher launcher = new Launcher(fileHashMap.commands, new StringParser() {
            @Override
            public String[] parse(String string) {
                String[] stringList = string.trim().split("[\\s]+");
                if (stringList.length < 2 || !stringList[0].equals("put")) {
                    return stringList;
                } else {
                    String[] newStringList = new String[3];
                    newStringList[0] = stringList[0];
                    newStringList[1] = stringList[1];
                    newStringList[2] = string.replaceFirst("[\\s]*put[\\s]+" + stringList[1] + "\\s", "");
                    return newStringList;
                }
            }
        });
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
