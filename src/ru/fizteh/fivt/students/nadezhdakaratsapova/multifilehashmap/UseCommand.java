package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class UseCommand implements Command {
    private MultiFileHashMapProvider curState;

    public UseCommand(MultiFileHashMapProvider state) {
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
        try {
            if (curState.setCurTable(args[1]) != null) {
                System.out.println("using " + args[1]);
            } else {
                System.out.println(args[1] + " not exists");
            }
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
    }

    public int getArgsCount() {
        return 1;
    }
}
