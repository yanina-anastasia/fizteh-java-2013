package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.IOException;
import java.io.RandomAccessFile;

public class CloseFile {
    public static void closeFile(RandomAccessFile file) {
        try {
            file.close();
        } catch (IOException e2) {
            System.err.println("Can't close file ");
        }
    }
}
