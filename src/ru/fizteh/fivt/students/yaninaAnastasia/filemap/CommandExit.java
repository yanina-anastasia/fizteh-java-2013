package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class CommandExit extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        DBState myState = DBState.class.cast(curState);
        if (myState.table == null) {
            System.err.println("no table");
            return false;
        }
        if (args.length != 0) {
            System.err.println("Invalid arguments");
            return false;
        }
        long offset = 0;
        long cursor = 0;
        Set<String> keys = myState.table.keySet();
        for (String step : keys) {
            offset += step.getBytes("UTF-8").length + 8;
        }
        for (Map.Entry<String, String> step : myState.table.entrySet()) {
            myState.dbFile.seek(cursor);
            myState.dbFile.writeUTF(step.getKey());
            myState.dbFile.writeChar('\0');
            myState.dbFile.writeInt((int) offset);
            cursor = myState.dbFile.getFilePointer();
            myState.dbFile.seek(offset);
            myState.dbFile.writeUTF(step.getValue());
            offset = myState.dbFile.getFilePointer();
        }
        System.exit(0);
        return true;
    }

    public String getCmd() {
        return "exit";
    }
}
