package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;

public class ExitCommand implements Command {
    MultiFileHashMapProvider curState;

    public ExitCommand(MultiFileHashMapProvider state) {
        curState = state;
    }

    public String getName() {
        return "exit";
    }

    public void execute(String[] args) throws IOException {
        if (curState.curDataBaseStorage != null) {
            curState.curDataBaseStorage.writeToDataBase();
        }
        System.exit(0);
    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 0);
    }
}
