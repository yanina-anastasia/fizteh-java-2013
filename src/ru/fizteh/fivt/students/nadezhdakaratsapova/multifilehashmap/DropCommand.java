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
        try {
            curState.removeTable(args[1]);
            if (curState.getCurTable() != null && args[1].equals(curState.dataStorage.getName())) {
                curState.setCurTable(null);
            }
            System.out.println("dropped");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public int getArgsCount() {
        return 1;
    }


}
