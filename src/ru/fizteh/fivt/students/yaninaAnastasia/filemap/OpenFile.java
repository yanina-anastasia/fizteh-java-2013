package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class OpenFile {
    public static boolean open(State curState) throws IOException {
        DBState myState = DBState.class.cast(curState);
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.println("Error with getting property");
            System.exit(1);
        }
        if (!(new File(path).exists())) {
            System.err.println("The path from the property does not exist");
            System.exit(1);
        }
        if (new File(path).isFile()) {
            System.err.println("The path from the property is not a directory");
            System.exit(1);
        }
        myState.workingDirectory = new File(path, "db.dat").toString();
        File tmpFile = new File(curState.workingDirectory);
        RandomAccessFile temp = null;

        if (!tmpFile.exists()) {
            if (!tmpFile.createNewFile()) {
                System.err.println("Error with creating a directory");
                return false;
            } else {
                temp = new RandomAccessFile(tmpFile, "rw");
            }
        } else {
            try {
                loadTable(temp, myState);
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

    private static void loadTable(RandomAccessFile temp, DBState curState) throws IOException {
        temp = new RandomAccessFile(curState.workingDirectory, "rw");
        if (temp.length() == 0) {
            return;
        }
        long nextOffset = 0;

        temp.seek(0);
        byte c = temp.readByte();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (c != 0) {
            out.write(c);
            c = temp.readByte();
        }
        String key = new String(out.toByteArray(), StandardCharsets.UTF_8);
        long firstOffset = temp.readInt();
        long currentOffset = firstOffset;
        long cursor = temp.getFilePointer();
        String nextKey = key;

        while (cursor < firstOffset) {
            c = temp.readByte();
            out = new ByteArrayOutputStream();
            while (c != 0) {
                out.write(c);
                c = temp.readByte();
            }
            nextKey = new String(out.toByteArray(), StandardCharsets.UTF_8);
            nextOffset = temp.readInt();
            cursor = temp.getFilePointer();
            temp.seek(currentOffset);
            int len = (int) (nextOffset - currentOffset);
            if (len < 0) {
                throw new IOException("File has incorrect format");
            }
            byte[] bytes = new byte[len];
            temp.read(bytes);
            String putValue = new String(bytes, StandardCharsets.UTF_8);
            curState.table.put(key, putValue);
            temp.seek(cursor);
            key = nextKey;
            currentOffset = nextOffset;
        }
        temp.seek(currentOffset);
        int len = (int) (temp.length() - currentOffset);
        if (len < 0) {
            throw new IOException("File has incorrect format");
        }
        byte[] bytes = new byte[len];
        temp.read(bytes);
        String putValue = new String(bytes, StandardCharsets.UTF_8);
        curState.table.put(nextKey, putValue);
        temp.close();
    }
}
