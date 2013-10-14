package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;

import ru.fizteh.fivt.students.sterzhanovVladislav.shell.Command;
import ru.fizteh.fivt.students.sterzhanovVladislav.shell.Shell;
import ru.fizteh.fivt.students.sterzhanovVladislav.shell.ShellCommands;
import ru.fizteh.fivt.students.sterzhanovVladislav.shell.ShellUtility;

public class Wrapper {

    private static HashMap<String, Command> cmdMap = new HashMap<String, Command>();
    private static DatabaseContext context = null;
    
    public static void main(String[] args) {
        try {
            context = new DatabaseContext(Paths.get(System.getProperty("fizteh.db.dir")));
        } catch (NullPointerException e) {
            System.out.println("fizteh.db.dir not set");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        
        cmdMap.put("put", new FileMapCommands.Put().setContext(context));
        cmdMap.put("get", new FileMapCommands.Get().setContext(context));
        cmdMap.put("remove", new FileMapCommands.Remove().setContext(context));
        cmdMap.put("exit", new ShellCommands.Exit());
        
        try {
            Shell cmdShell = new Shell(cmdMap);
            if (args.length > 0) {
                InputStream cmdStream = ShellUtility.createStream(args);
                cmdShell.execCommandStream(cmdStream, false);
            } else {
                cmdShell.execCommandStream(System.in, true);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        System.exit(0);
    }
}
