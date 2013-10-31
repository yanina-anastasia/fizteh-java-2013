package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CommandDrop extends Command {
    public static boolean recRemove(File file) throws IOException {
        if (file.isDirectory()) {
            for (File innerFile : file.listFiles()) {
                recRemove(innerFile);
            }
        }
        try {
            Files.delete(Paths.get(file.getAbsolutePath()));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean exec(String[] args, State curState) throws IOException {
        MultiDBState myState = MultiDBState.class.cast(curState);
        if (args.length != 1) {
            System.err.println("Invalid arguments");
            return false;
        }
        String path = myState.getProperty(myState);
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
            for (String step : myState.myDatabase.database.keySet()) {
                myState.table = myState.myDatabase.database.get(step);
                myState.curTableName = step;
                if (myState.table != null) {
                    MultiFileMapUtils saver = new MultiFileMapUtils();
                    if (!saver.save(myState)) {
                        System.err.println("Previous file was not saved");
                        return false;
                    }
                }
            }
            myState.table = null;
            myState.curTableName = "";
        }
        System.out.println("dropped");

        return true;
    }

    public String getCmd() {
        return "drop";
    }
}
