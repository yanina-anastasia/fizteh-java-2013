package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
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
        myState.dbFile = new RandomAccessFile(curState.workingDirectory, "rw");
        myState.dbFile.setLength(0);
        Set<String> keys = myState.table.keySet();
        for (String step : keys) {
            offset += step.getBytes(StandardCharsets.UTF_8).length + 5;
        }
        for (String step : myState.table.keySet()) {
            byte[] bytesToWrite = step.getBytes(StandardCharsets.UTF_8);
            myState.dbFile.write(bytesToWrite);
            myState.dbFile.writeByte(0);
            myState.dbFile.writeInt((int) offset);
            offset += myState.table.get(step).getBytes(StandardCharsets.UTF_8).length;
        }
        for (String key : myState.table.keySet()) {
            String value = myState.table.get(key);
            myState.dbFile.write(value.getBytes(StandardCharsets.UTF_8));
        }
        if (myState.dbFile.length() == 0) {
            new File(myState.workingDirectory).delete();
        }
        myState.dbFile.close();
        System.exit(0);
        return true;
    }

    public String getCmd() {
        return "exit";
    }
}
