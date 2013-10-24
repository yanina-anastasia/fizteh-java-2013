package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.IOException;

public class CommandMultiExit extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        MultiDBState myState = MultiDBState.class.cast(curState);
        if (args.length != 0) {
            System.err.println("Invalid arguments");
            return false;
        }
        myState.table = myState.myDatabase.database.get(myState.curTableName);
        if (MultiFileMapUtils.save(myState)) {
            System.exit(0);
            return true;
        } else {
            System.err.println("File has not been saved");
            System.exit(0);
            return false;
        }
    }

    public String getCmd() {
        return "exit";
    }
}
