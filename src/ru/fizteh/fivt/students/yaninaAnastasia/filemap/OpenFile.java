package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class OpenFile {
    public static boolean open(DBState curState) throws IOException {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.println("Error with getting property");
        }
        if (!(new File(path).exists())) {
            System.err.println("The path from the property does not exist");
            System.exit(1);
        }
        if (new File(path).isFile()) {
            System.err.println("The path from the property is not a directory");
            System.exit(1);
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
            } catch (IOException e) {
                System.err.println("Wrong format");
                return false;
            }
        }
        return true;
    }

    private static void loadTable(DBState curState) throws IOException {
        curState.dbFile = new RandomAccessFile(curState.workingDirectory, "rw");
        if (curState.dbFile.length() == 0) {
            return;
        }
        long nextOffset = 0;

        curState.dbFile.seek(0);
        byte c = curState.dbFile.readByte();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (c != 0) {
            out.write(c);
            c = curState.dbFile.readByte();
        }
        String key = new String(out.toByteArray(), StandardCharsets.UTF_8);
        long firstOffset = curState.dbFile.readInt();
        long currentOffset = firstOffset;
        long cursor = curState.dbFile.getFilePointer();
        String nextKey = key;

        while (cursor < firstOffset) {
            c = curState.dbFile.readByte();
            out = new ByteArrayOutputStream();
            while (c != 0) {
                out.write(c);
                c = curState.dbFile.readByte();
            }
            nextKey = new String(out.toByteArray(), StandardCharsets.UTF_8);
            nextOffset = curState.dbFile.readInt();
            cursor = curState.dbFile.getFilePointer();
            curState.dbFile.seek(currentOffset);
            int len = (int) (nextOffset - currentOffset);
            if (len < 0) {
                throw new IOException("File has incorrect format");
            }
            byte[] bytes = new byte[len];
            curState.dbFile.read(bytes);
            String putValue = new String(bytes, StandardCharsets.UTF_8);
            curState.table.put(key, putValue);
            curState.dbFile.seek(cursor);
            key = nextKey;
            currentOffset = nextOffset;
        }
        curState.dbFile.seek(currentOffset);
        int len = (int) (curState.dbFile.length() - currentOffset);
        if (len < 0) {
            throw new IOException("File has incorrect format");
        }
        byte[] bytes = new byte[len];
        curState.dbFile.read(bytes);
        String putValue = new String(bytes, StandardCharsets.UTF_8);
        curState.table.put(nextKey, putValue);
        curState.dbFile.close();
    }
}
