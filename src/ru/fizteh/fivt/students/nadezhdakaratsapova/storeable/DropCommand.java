package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;
import java.text.ParseException;

public class DropCommand implements Command {
    StoreableTableProvider curState;

    public DropCommand(StoreableTableProvider state) {
        curState = state;
    }

    public String getName() {
        return "drop";
    }

    public void execute(String[] args) throws IOException {
        curState.removeTable(args[1]);
        if (curState.curDataBaseStorage != null && args[1].equals(curState.curDataBaseStorage.getName())) {
            curState.setCurTable(null);
        }
        System.out.println("dropped");
    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 1);
    }

}
