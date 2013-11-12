package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable;


import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap.MultiFileHashMapProvider;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;
import java.text.ParseException;

public class PutCommand implements Command {
    private StoreableTableProvider curState;

    public PutCommand(StoreableTableProvider state) {
        curState = state;
    }

    public String getName() {
        return "put";
    }

    public void execute(String[] args) throws IOException {
        if (curState.curDataBaseStorage != null) {
            Storeable value = curState.curDataBaseStorage.get(args[1]);
            try {
                curState.curDataBaseStorage.put(args[1], curState.deserialize(curState.curDataBaseStorage, args[2]));

                if (value == null) {
                    System.out.println("new");
                } else {
                    System.out.println("overwrite");
                    System.out.println(value);
                }
            } catch (ParseException e) {
                System.err.println("put command: " + e.getMessage());
            }
        } else {
            System.out.println("no table");
        }
    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 2);
    }
}
