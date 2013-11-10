package ru.fizteh.fivt.students.fedoseev.filemap;

import java.io.File;

public class FileMapState {
    private File curFile;

    public void setCurState(File file) {
        curFile = file;
    }

    public File getCurState() {
        return curFile;
    }
}
