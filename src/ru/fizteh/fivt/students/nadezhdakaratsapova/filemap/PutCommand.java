package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;

public class PutCommand implements Command {

    private FileMapState curState;

    public PutCommand(FileMapState state) {
        curState = state;
    }

    public String getName() {
        return "put";
    }

    public void execute(String[] args) throws IOException {
        String value = curState.dataStorage.getValue(args[1]);
        curState.dataStorage.add(args[1], args[2]);
        if (value == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(value);
        }
    }

    public int getArgsCount() {
        return 2;
    }

}
