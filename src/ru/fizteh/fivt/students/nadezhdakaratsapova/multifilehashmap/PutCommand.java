package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;

public class PutCommand implements Command {
    private MultiFileHashMapProvider curState;

    public PutCommand(MultiFileHashMapProvider state) {
        curState = state;
    }

    public String getName() {
        return "put";
    }

    public void execute(String[] args) throws IOException {
        if (curState.curDataBaseStorage != null) {
            String value = curState.curDataBaseStorage.get(args[1]);
            curState.curDataBaseStorage.put(args[1], args[2]);
            if (value == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
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
