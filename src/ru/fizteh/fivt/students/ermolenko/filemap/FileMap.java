package ru.fizteh.fivt.students.ermolenko.filemap;

import ru.fizteh.fivt.students.ermolenko.shell.Shell;

import java.io.File;
import java.io.IOException;

public class FileMap extends Shell {

    private FileMapState state;

    public FileMapState getFileMapState() {
        return state;
    }

    public FileMap(File currentFile) throws IOException {
        state = new FileMapState(currentFile);
        System.out.println("3");
        FileMapUtils.readDataBase(state);
    }
}