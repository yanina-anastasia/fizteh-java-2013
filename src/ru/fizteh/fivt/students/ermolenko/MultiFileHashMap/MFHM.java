package ru.fizteh.fivt.students.ermolenko.MultiFileHashMap;

import ru.fizteh.fivt.students.ermolenko.shell.Shell;

import java.io.File;
import java.io.IOException;

public class MFHM extends Shell {

    private MFHMState state;

    public MFHM(File currentFile) throws IOException {

        state = new MFHMState(currentFile);
    }

    public MFHMState getMFHMState() {

        return state;
    }
}
