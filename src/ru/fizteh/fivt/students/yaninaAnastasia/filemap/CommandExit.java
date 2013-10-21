package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class CommandExit extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        RandomAccessFile temp = new RandomAccessFile(curState.workingDirectory, "rw");
        try {
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

            temp.setLength(0);
            Set<String> keys = myState.table.keySet();
            for (String step : keys) {
                offset += step.getBytes(StandardCharsets.UTF_8).length + 5;
            }
            for (String step : myState.table.keySet()) {
                byte[] bytesToWrite = step.getBytes(StandardCharsets.UTF_8);
                temp.write(bytesToWrite);
                temp.writeByte(0);
                temp.writeInt((int) offset);
                offset += myState.table.get(step).getBytes(StandardCharsets.UTF_8).length;
            }
            for (String key : myState.table.keySet()) {
                String value = myState.table.get(key);
                temp.write(value.getBytes(StandardCharsets.UTF_8));
            }
            if (temp.length() == 0) {
                new File(myState.workingDirectory).delete();
            }
        } catch (IOException e) {
            System.err.println("Error while writing file");
            return false;
        } finally {
            try {
                temp.close();
            } catch (IOException t) {
                System.err.println("Error while closing file");
                System.exit(1);
            }
        }
        return true;
    }

    public String getCmd() {
        return "exit";
    }
}
