package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class CommandCreate extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        MultiDBState myState = MultiDBState.class.cast(curState);
        if (args.length != 1) {
            System.err.println("Invalid arguments");
            return false;
        }
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.println("Error with getting property");
            System.exit(1);
        }
        if (myState.myDatabase.database.containsKey(args[0])) {
            System.out.println(args[0] + " exists");
            return false;
        }
        File temp = new File(path, args[0]);
        if (!temp.exists()) {
            temp.mkdir();
        } else {
            System.out.println(args[0] + " exists");
            return false;
        }
        myState.myDatabase.database.put(args[0], new HashMap<String, String>());
        System.out.print("created");
        return true;
    }

    public String getCmd() {
        return "create";
    }
}
