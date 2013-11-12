package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;


import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;

public class CommitCommand implements Command {
    private MultiFileHashMapProvider curState;

    public CommitCommand(MultiFileHashMapProvider state) {
        curState = state;
    }

    public String getName() {
        return "commit";
    }

    public void execute(String[] args) throws IOException {
        System.out.println(curState.curDataBaseStorage.commit());
    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 0);
    }
}
