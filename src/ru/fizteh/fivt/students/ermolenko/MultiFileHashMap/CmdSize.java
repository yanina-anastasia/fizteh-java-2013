package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.IOException;

public class CmdSize implements Command<MultiFileHashMapState> {

    @Override
    public String getName() {

        return "size";
    }

    @Override
    public void executeCmd(MultiFileHashMapState inState, String[] args) throws IOException {


    }
}
