package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;

public class RemoveCommand implements Command {

    private FileMapState curState;

    public RemoveCommand(FileMapState state) {
        curState = state;
    }

    public String getName() {
        return "remove";
    }

    public void execute(String[] args) throws IOException {
        String value = curState.remove(args[1]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }

    public int getArgsCount() {
        return 1;
    }
}
