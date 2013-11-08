package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable;

import ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap.MultiFileHashMapProvider;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;

public class RollbackCommand implements Command {
    private StoreableTableProvider curState;

    public RollbackCommand(StoreableTableProvider state) {
        curState = state;
    }

    public String getName() {
        return "rollback";
    }

    public void execute(String[] args) throws IOException {
        System.out.println(curState.curDataBaseStorage.rollback());
    }

    public int getArgsCount() {
        return 0;
    }

}
