package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;


import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;

public class SizeCommand implements Command {
    MultiFileHashMapProvider curState;

    public SizeCommand(MultiFileHashMapProvider state) {
        curState = state;
    }

    public String getName() {
        return "size";
    }

    public void execute(String[] args) throws IOException {
        System.out.println(curState.dataStorage.size());
    }

    public int getArgsCount() {
        return 0;
    }
}
