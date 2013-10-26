package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.IOException;

public class CommandUse extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        MultiDBState myState = MultiDBState.class.cast(curState);
        if (args.length != 1) {
            System.err.println("Invalid arguments");
            return false;
        }
        String path = myState.getProperty();
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
