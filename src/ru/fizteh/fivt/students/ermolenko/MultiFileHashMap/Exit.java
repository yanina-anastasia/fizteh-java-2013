package ru.fizteh.fivt.students.ermolenko.MultiFileHashMap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;
import ru.fizteh.fivt.students.ermolenko.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Exit implements Command {

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public void executeCmd(Shell shell, String[] args) throws IOException {
        if ((((MFHM) shell).getMFHMState().getCurrentTable()) != null) {
            File fileForWrite = ((MFHMTable) ((MFHM) shell).getMFHMState().getCurrentTable()).getDataFile();
            Map<String, String> mapForWrite = ((MFHMTable) ((MFHM) shell).getMFHMState().getCurrentTable()).getDataBase();
            MFHMUtils.write(fileForWrite, mapForWrite);
        }
        System.exit(0);
    }
}
