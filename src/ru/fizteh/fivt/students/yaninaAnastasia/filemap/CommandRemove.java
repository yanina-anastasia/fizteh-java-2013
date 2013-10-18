package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.IOException;

public class CommandRemove extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        DBState myState = DBState.class.cast(curState);
        if (myState.table == null) {
            System.err.println("no table");
            return false;
        }
        if (args.length != 1) {
            System.err.println("Invalid arguments");
            return false;
        }
        String value = myState.table.remove(args[0]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
        return true;
    }

    public String getCmd() {
        return "remove";
    }
}
