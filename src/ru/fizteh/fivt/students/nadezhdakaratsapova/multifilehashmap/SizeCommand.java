package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;


import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;

public class SizeCommand implements Command {
    private MultiFileHashMapProvider curState;

    public SizeCommand(MultiFileHashMapProvider state) {
        curState = state;
    }

    public String getName() {
        return "size";
    }

    public void execute(String[] args) throws IOException {
        System.out.println(curState.curDataBaseStorage.size());
    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 0);
    }
}
