package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.File;
import java.io.IOException;

public class UseCommand implements Command {
    private MultiFileHashMapProvider curState;

    public UseCommand(MultiFileHashMapProvider state) {
        curState = state;
    }

    public String getName() {
        return "use";
    }

    public void execute(String[] args) throws IOException {
        File newTable = new File(curState.getWorkingDirectory(), args[1]);
        if (newTable.getCanonicalFile().exists()) {
            curState.setNextTable(newTable.getCanonicalFile());
            System.out.println("using " + args[1]);
        } else {
            System.out.println(args[1] + " not exists");
        }

    }

    public int getArgsCount() {
        return 1;
    }
}
