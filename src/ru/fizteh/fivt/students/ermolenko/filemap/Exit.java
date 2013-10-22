package ru.fizteh.fivt.students.ermolenko.filemap;

import ru.fizteh.fivt.students.ermolenko.shell.Shell;
import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.IOException;

public class Exit implements Command {

    public String getName() {
        return "exit";
    }

    public void executeCmd(Shell filemap, String[] args) throws IOException {
        FileMapUtils.write(((FileMap) filemap).getFileMapState().getDataBase(), ((FileMap) filemap).getFileMapState().getDataFile());
        System.exit(0);
    }
}
