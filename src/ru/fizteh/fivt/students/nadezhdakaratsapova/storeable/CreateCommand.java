package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable;

import ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap.MultiFileHashMapProvider;
import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.SignatureController;

import java.io.IOException;

public class CreateCommand implements Command {
    StoreableTableProvider curState;

    public CreateCommand(StoreableTableProvider state) {
        curState = state;
    }

    public String getName() {
        return "create";
    }

    public void execute(String[] args) throws IOException {
        if (curState.createTable(args[1], SignatureController.getColumnTypes(curState.curDataBaseStorage)) == null) {
            System.out.println(args[1] + " exists");
        } else {
            System.out.println("created");
        }
    }

    public int getArgsCount() {
        return 1;
    }
}
