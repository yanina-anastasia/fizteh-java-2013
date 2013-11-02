package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;


import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;

public class CommitCommand implements Command {
    MultiFileHashMapProvider curState;

    public CommitCommand(MultiFileHashMapProvider state) {
        curState = state;
    }

    public String getName() {
        return "commit";
    }

    public void execute(String[] args) throws IOException {
        System.out.println(curState.curDataBaseStorage.commit());
    }

    public int getArgsCount() {
        return 0;
    }
}
