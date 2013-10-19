package ru.fizteh.fivt.students.valentinbarishev.multifilehashmap;

import ru.fizteh.fivt.students.valentinbarishev.filemap.DataBaseFile;

public class MultiDataBaseFile extends DataBaseFile {

    private int fileNumber;
    private int direcotryNumber;

    public MultiDataBaseFile(final String fileName, final int newFileNumber, final int newDirectoryNumber){
        super(fileName);
        fileNumber = newFileNumber;
        direcotryNumber = newDirectoryNumber;
    }

    public boolean check() {
        for (Node node : data) {
            if (!((node.getZeroByte() % 16 == direcotryNumber) && ((node.getZeroByte() / 16) % 16 == fileNumber))) {
                return false;
            }
        }
        return true;
    }
}
