package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;

public class ExitCommand implements Command {
    MultiFileHashMapState curState;
    DataWriter dataWriter = new DataWriter();

    public ExitCommand(MultiFileHashMapState state) {
        curState = state;
    }

    public String getName() {
        return "exit";
    }

    public void execute(String[] args) throws IOException {
        if (curState.getCurTable() != null) {
            dataWriter.writeData(curState);
        }
        System.exit(0);
    }

    public int getArgsCount() {
        return 0;
    }
}
