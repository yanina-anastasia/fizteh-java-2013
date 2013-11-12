package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap.MultiFileHashMapProvider;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;
import java.text.ParseException;

public class RemoveCommand implements Command {
    private StoreableTableProvider curState;

    public RemoveCommand(StoreableTableProvider state) {
        curState = state;
    }

    public String getName() {
        return "remove";
    }

    public void execute(String[] args) throws IOException {
        if (curState.curDataBaseStorage != null) {
            Storeable value = curState.curDataBaseStorage.remove(args[1]);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }

        } else {
            System.out.println("no table");
        }
    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 1);
    }
}
