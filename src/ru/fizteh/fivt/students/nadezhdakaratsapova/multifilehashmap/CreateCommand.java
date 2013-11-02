package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;


import java.io.IOException;

public class CreateCommand implements Command {
    MultiFileHashMapProvider curState;

    public CreateCommand(MultiFileHashMapProvider state) {
        curState = state;
    }

    public String getName() {
        return "create";
    }

    public void execute(String[] args) throws IOException {
        if (curState.createTable(args[1]) == null) {
            System.out.println(args[1] + " exists");
        } else {
            System.out.println("created");
        }
    }

    public int getArgsCount() {
        return 1;
    }


}
