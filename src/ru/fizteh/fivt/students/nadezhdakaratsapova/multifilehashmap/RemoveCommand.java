package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;

public class RemoveCommand implements Command {
    private MultiFileHashMapProvider curState;

    public RemoveCommand(MultiFileHashMapProvider state) {
        curState = state;
    }

    public String getName() {
        return "remove";
    }

    public void execute(String[] args) throws IOException {
        if (curState.curDataBaseStorage != null) {
            String value = curState.curDataBaseStorage.remove(args[1]);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        } else {
            System.out.println("no table");
        }
    }

    public int getArgsCount() {
        return 1;
    }
}
