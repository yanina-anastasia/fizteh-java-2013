package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.students.ermolenko.shell.Shell;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMap extends Shell {

    private MultiFileHashMapState state;

    public MultiFileHashMap(File currentFile) throws IOException {

        state = new MultiFileHashMapState(currentFile);
    }

    public MultiFileHashMapState getMFHMState() {

        return state;
    }
}
