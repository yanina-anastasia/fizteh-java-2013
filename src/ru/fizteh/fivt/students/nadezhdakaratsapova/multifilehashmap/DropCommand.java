package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;


import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;


import java.io.IOException;
import java.text.ParseException;

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
            try {
                curState.setCurTable(null);
            } catch (ParseException e) {
                throw new IOException(e.getMessage());
            }
        }
        System.out.println("dropped");
    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 1);
    }
}
