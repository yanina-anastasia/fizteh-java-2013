package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;


import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.CommandUtils;

import java.io.File;
import java.io.IOException;

public class DropCommand implements Command {
    MultiFileHashMapState curState;

    public DropCommand(MultiFileHashMapState state) {
        curState = state;
    }

    public String getName() {
        return "drop";
    }

    public void execute(String[] args) throws IOException {
        File table = new File(curState.getWorkingDirectory(), args[1]);
        table = table.getCanonicalFile();
        if (!table.exists()) {
            throw new IOException(args[1] + " doesn't exist");
        }
        if (!table.isDirectory()) {
            throw new IOException("table " + args[1] + " should be a directory");
        }
        CommandUtils.recDeletion(table);
    }

    public int getArgsCount() {
        return 1;
    }


}
