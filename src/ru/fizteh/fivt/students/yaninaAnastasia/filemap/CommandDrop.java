package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.File;
import java.io.IOException;

public class CommandDrop extends Command {
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
        if (!myState.myDatabase.database.containsKey(args[0])) {
            System.out.println(args[0] + " not exists");
            return false;
        }
        File temp = new File(path, args[0]);
        if (temp.exists()) {
            for (File stepDir: temp.listFiles()) {
                for (File stepFile: stepDir.listFiles()) {
                    if (stepFile.exists()) {
                        stepFile.delete();
                    }
                }
                if (stepDir.exists()) {
                    stepDir.delete();
                }
            }
            if (temp.length() == 0) {
                temp.delete();
            } else {
                System.err.println("The " + args[0] + " directory was not deleted");
            }
        } else {
            System.out.println(args[0] + " not exists");
            return false;
        }
        myState.myDatabase.database.remove(args[0]);
        if (args[0].equals(myState.curTableName)) {
            myState.table = null;
            myState.curTableName = "";
        }
        System.out.print("dropped");
        return true;
    }

    public String getCmd() {
        return "drop";
    }
}
