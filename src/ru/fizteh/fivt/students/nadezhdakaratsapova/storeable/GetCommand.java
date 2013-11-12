package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap.MultiFileHashMapProvider;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;

public class GetCommand implements Command {

    StoreableTableProvider curState;

    public GetCommand(StoreableTableProvider state) {
        curState = state;
    }

    public String getName() {
        return "get";
    }

    public void execute(String[] args) throws IOException {
        if (curState.curDataBaseStorage != null) {
            Storeable value = curState.curDataBaseStorage.get(args[1]);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(value);
            }
        } else {
            System.out.println("no table");
        }
    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 2);
    }
}
