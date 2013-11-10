package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;

public class GetCommand implements Command {

    private FileMapState curState;

    public GetCommand(FileMapState state) {
        curState = state;
    }

    public String getName() {
        return "get";
    }

    public void execute(String[] args) throws IOException {
        String value = curState.dataStorage.get(args[1]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }

    public int getArgsCount() {
        return 1;
    }
}
