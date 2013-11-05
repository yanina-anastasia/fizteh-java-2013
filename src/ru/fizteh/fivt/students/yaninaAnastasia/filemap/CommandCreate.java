package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.File;
import java.io.IOException;

public class CommandCreate extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        MultiDBState myState = MultiDBState.class.cast(curState);
        if (args.length != 1) {
            throw new IllegalArgumentException("Illegal arguments");
        }
        String path = myState.getProperty(myState);
        if (myState.database.tables.containsKey(args[0])) {
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
        myState.database.createTable(args[0]);
        System.out.println("created");
        return true;
    }

    public String getCmd() {
        return "create";
    }
}
