package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;


import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.File;
import java.io.IOException;

public class UseCommand implements Command {
    private MultiFileHashMapState curState;

    public UseCommand(MultiFileHashMapState state) {
        curState = state;
    }

    public String getName() {
        return "use";
    }

    public void execute(String[] args) throws IOException {
        File newTable = new File(curState.getWorkingDirectory(), args[1]);
        curState.setNextTable(newTable.getCanonicalFile());
    }

    public int getArgsCount() {
        return 1;
    }
}
