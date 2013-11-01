package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.File;
import java.io.IOException;


public class CommandDrop extends Command {
    public static boolean recRemove(File file) throws IOException {
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
        if (args.length != 1) {
            throw new IllegalArgumentException("Illegal arguments");
        }
        String path = myState.getProperty(myState);
        if (!myState.database.tables.containsKey(args[0])) {
            System.out.println(args[0] + " not exists");
            return false;
        }
        myState.database.tables.remove(args[0]);
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

        if (args[0].equals(myState.curTableName)) {
            myState.table = null;
            myState.table.putName("");
        }
        System.out.println("dropped");
        return true;
    }

    public String getCmd() {
        return "drop";
    }
}
