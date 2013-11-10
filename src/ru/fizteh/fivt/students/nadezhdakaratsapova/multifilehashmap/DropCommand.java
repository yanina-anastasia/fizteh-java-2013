package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;


import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;


import java.io.IOException;

public class DropCommand implements Command {
    MultiFileHashMapProvider curState;

    public DropCommand(MultiFileHashMapProvider state) {
        curState = state;
    }

    public String getName() {
        return "drop";
    }

    public void execute(String[] args) throws IOException {
        curState.removeTable(args[1]);
        if (curState.curDataBaseStorage != null && args[1].equals(curState.curDataBaseStorage.getName())) {
            curState.setCurTable(null);
        }
        System.out.println("dropped");
    }

    public int getArgsCount() {
        return 1;
    }


}
