package ru.fizteh.fivt.students.valentinbarishev.multifilehashmap;

import ru.fizteh.fivt.students.valentinbarishev.filemap.DataBaseFile;
import ru.fizteh.fivt.students.valentinbarishev.filemap.DataBaseWrongFileFormat;

public class MultiDataBaseFile extends DataBaseFile {

    private int fileNumber;
    private int direcotryNumber;

    public MultiDataBaseFile(final String fileName, final int newDirectoryNumber, final int newFileNumber){
        super(fileName);
        fileNumber = newFileNumber;
        direcotryNumber = newDirectoryNumber;
        check();
    }

    public boolean check() {
        for (Node node : data) {
            if (!((node.getZeroByte() % 16 == direcotryNumber) && ((node.getZeroByte() / 16) % 16 == fileNumber))) {
                throw new DataBaseWrongFileFormat("Wrong file format key[0] =  " + String.valueOf(node.getZeroByte())
                        + " in file " + fileName);
            }
        }
        return true;
    }

    public void close() {
        data.clear();
    }
}
