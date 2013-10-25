package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.File;
import java.io.IOException;


public class CommandDrop extends Command {
    private boolean recRemove(File file) throws IOException {
        if (file.isDirectory()) {
            for (File innerFile : file.listFiles()) {
                recRemove(innerFile);
            }
        }
        if (!file.delete()) {
            System.err.println("Error while deleting");
            return false;
        }
        return true;
    }

    public boolean exec(String[] args, State curState) throws IOException {
        MultiDBState myState = MultiDBState.class.cast(curState);
        if (!myState.checkArgs(args, 1)) {
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
            File file = temp;
            if (!recRemove(file)) {
                System.err.println("File was not deleted");
                return false;
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
