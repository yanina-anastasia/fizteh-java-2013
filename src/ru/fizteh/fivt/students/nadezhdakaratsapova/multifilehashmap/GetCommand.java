package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;


import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;

public class GetCommand implements Command {

    MultiFileHashMapState curState;

    public GetCommand(MultiFileHashMapState state) {
        curState = state;
    }

    public String getName() {
        return "get";
    }

    public void execute(String[] args) throws IOException {
        DataLoader dataLoader = new DataLoader();
        dataLoader.load(curState);
        if (curState.getCurTable() != null) {
            String value = curState.dataStorage.getValue(args[1]);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(value);
            }
        }
    }

    public int getArgsCount() {
        return 1;
    }
}
