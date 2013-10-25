package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;


import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.File;
import java.io.IOException;

public class CreateCommand implements Command {
    MultiFileHashMapState curState;

    public CreateCommand(MultiFileHashMapState state) {
        curState = state;
    }

    public String getName() {
        return "create";
    }

    public void execute(String[] args) throws IOException {
        File newTable = new File(curState.getWorkingDirectory(), args[1]);
        newTable = newTable.getCanonicalFile();
        if (newTable.exists()) {
            if (!newTable.isDirectory()) {
                throw new IOException(args[1] + " should be a directory");
            }
            System.out.println(args[1] + " exists");
        } else {
            newTable.mkdir();
            System.out.println("created");
        }
    }

    public int getArgsCount() {
        return 1;
    }


}
