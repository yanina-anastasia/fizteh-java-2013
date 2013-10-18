package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class OpenFile {
    public static boolean open(DBState curState) throws IOException {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.println("Error with getting property");
        }
        curState.workingDirectory = new File(path, "db.dat").toString();
        File tmpFile = new File(curState.workingDirectory);

        if (!tmpFile.exists()) {
            if (!tmpFile.createNewFile()) {
                System.err.println("Error with creating a directory");
                return false;
            } else {
                curState.dbFile = new RandomAccessFile(tmpFile, "rw");
            }
        } else {
            try {
                loadTable(curState);
            } catch (EOFException e) {
                System.err.println("Wrong format");
                return false;
            }
        }
        return true;
    }

    private static boolean loadTable(DBState curState) throws IOException {
        curState.dbFile = new RandomAccessFile(curState.workingDirectory, "rw");
        if (curState.dbFile.length() == 0) {
            return false;
        }
        String key = null;
        String value = null;
        long curOffset;
        long offset = 0;
        boolean flag = true;
        long cursor = 0;

        do {
            curState.dbFile.seek(cursor);
            key = curState.dbFile.readUTF();
            curState.dbFile.readChar();
            curOffset = curState.dbFile.readInt();
            if (flag) {
                offset = curOffset;
                flag = false;
            }
            cursor = curState.dbFile.getFilePointer();
            curState.dbFile.seek(curOffset);
            value = curState.dbFile.readUTF();
            curState.table.put(key, value);
        } while (cursor < offset);
        return true;
    }
}
