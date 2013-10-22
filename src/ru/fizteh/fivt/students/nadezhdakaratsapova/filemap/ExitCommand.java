package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;

import java.io.IOException;

public class ExitCommand implements Command {

    private FileMapState curState;
    private FileWriter fileWriter = new FileWriter();

    public ExitCommand(FileMapState state) {
        curState = state;
    }

    public String getName() {
        return "exit";
    }

    public void execute(String[] args) throws IOException {
        fileWriter.writeDataToFile(curState);
        System.exit(0);
    }

    public int getArgsCount() {
        return 0;
    }
}
