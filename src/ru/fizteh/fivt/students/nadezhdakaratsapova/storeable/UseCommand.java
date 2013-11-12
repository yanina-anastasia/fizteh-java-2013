package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable;

import ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap.MultiFileHashMapProvider;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;

public class UseCommand implements Command {
    private StoreableTableProvider curState;

    public UseCommand(StoreableTableProvider state) {
        curState = state;
    }

    public String getName() {
        return "use";
    }

    public void execute(String[] args) throws IOException {
        int commitSize;
        if (curState.curDataBaseStorage != null) {
            if ((commitSize = curState.curDataBaseStorage.commitSize()) != 0) {
                throw new IOException(commitSize + " unsaved changes");
            }
        }
        if (curState.getTable(args[1]) != null) {
            curState.setCurTable(args[1]);
            System.out.println("using " + args[1]);
        } else {
            System.out.println(args[1] + " not exists");
        }

    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 1);
    }
}
