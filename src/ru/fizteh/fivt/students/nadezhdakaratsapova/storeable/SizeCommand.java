package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable;

import ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap.MultiFileHashMapProvider;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;

public class SizeCommand implements Command {
    private StoreableTableProvider curState;

    public SizeCommand(StoreableTableProvider state) {
        curState = state;
    }

    public String getName() {
        return "size";
    }

    public void execute(String[] args) throws IOException {
        System.out.println(curState.curDataBaseStorage.size());
    }

    public int getArgsCount() {
        return 0;
    }
}
