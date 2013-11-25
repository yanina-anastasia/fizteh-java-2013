package ru.fizteh.fivt.students.ermolenko786.filemap;

import ru.fizteh.fivt.students.ermolenko786.shell.Command;

import java.io.IOException;

public class Exit implements Command<FileMapState> {

    public String getName() {

        return "exit";
    }

    public void executeCmd(FileMapState inState, String[] args) throws IOException {

        FileMapUtils.write(inState.getDataBase(), inState.getDataFile());
        System.exit(0);
    }
}
