package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import ru.fizteh.fivt.students.sterzhanovVladislav.shell.Command;
import ru.fizteh.fivt.students.sterzhanovVladislav.shell.ShellCommands;
import ru.fizteh.fivt.students.sterzhanovVladislav.shell.ShellUtility;

public class Wrapper {

    private static HashMap<String, Command> cmdMap = new HashMap<String, Command>();
    private static DatabaseContext context = null;
    
    public static void main(String[] args) {
        String dbDir = System.getProperty("fizteh.db.dir");
        if (dbDir == null) {
            System.out.println("fizteh.db.dir not set");
            System.exit(-1);
        }
        Path dbPath = Paths.get(dbDir);
        if (dbPath == null) {
            System.out.println("fizteh.db.dir did not resolve to a valid directory");
            System.exit(-1);
        }
        try {
            context = new DatabaseContext(dbPath);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        
        cmdMap.put("put", new FileMapCommands.Put().setContext(context));
        cmdMap.put("get", new FileMapCommands.Get().setContext(context));
        cmdMap.put("remove", new FileMapCommands.Remove().setContext(context));
        cmdMap.put("exit", new ShellCommands.Exit());
        
        ShellUtility.execShell(args, cmdMap);
        System.exit(0);
    }
}
