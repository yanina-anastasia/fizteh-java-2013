package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;


import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;

public class RollbackCommand implements Command {
    MultiFileHashMapProvider curState;

    public RollbackCommand(MultiFileHashMapProvider state) {
        curState = state;
    }

    public String getName() {
        return "rollback";
    }

    public void execute(String[] args) throws IOException {
        System.out.println(curState.dataStorage.rollback());
    }

    public int getArgsCount() {
        return 0;
    }
}
