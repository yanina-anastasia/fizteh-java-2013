package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.IOException;

public class CommandUse extends Command {
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
        myState.curTableName = args[0];
        MultiFileMapUtils saver = new MultiFileMapUtils();
        if (!saver.save(myState)) {
            System.err.println("Previous file was not saved");
            return false;
        }
        myState.table = myState.myDatabase.database.get(args[0]);
        System.out.println("using " + args[0]);

        return true;
    }

    public String getCmd() {
        return "use";
    }
}
