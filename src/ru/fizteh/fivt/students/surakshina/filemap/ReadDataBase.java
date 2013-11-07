package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class ReadDataBase {
    public static HashMap<String, String> loadFile(File file) throws IOException {
        HashMap<String, String> mapFile = new HashMap<>();
        RandomAccessFile dataBase = null;
        try {
            dataBase = new RandomAccessFile(file, "rw");
            String keyFirst;
            String keySecond = null;
            String value;
            int currentPosition = (int) dataBase.getFilePointer();
            int offsetOfValueFirst = 0;
            int firstOffset = 0;
            int offsetOfValueSecond = 0;
            dataBase.seek(currentPosition);

            byte c = 0;
            ArrayList<Byte> vector = new ArrayList<Byte>();
            c = dataBase.readByte();
            while (c != 0) {
                vector.add(c);
                c = dataBase.readByte();
            }
            byte[] res = new byte[vector.size()];
            for (int i = 0; i < vector.size(); i++) {
                res[i] = vector.get(i).byteValue();
            }
            keyFirst = new String(res, StandardCharsets.UTF_8);

            offsetOfValueFirst = dataBase.readInt();
            if (offsetOfValueFirst > 0) {
                currentPosition = (int) dataBase.getFilePointer();
                firstOffset = offsetOfValueFirst;
                do {
                    dataBase.seek(currentPosition);
                    if (currentPosition < firstOffset) {
                        c = 0;
                        vector.clear();
                        c = dataBase.readByte();
                        while (c != 0) {
                            vector.add(c);
                            c = dataBase.readByte();
                        }
                        res = new byte[vector.size()];
                        for (int i = 0; i < vector.size(); i++) {
                            res[i] = vector.get(i).byteValue();
                        }
                        keySecond = new String(res, StandardCharsets.UTF_8);
                        offsetOfValueSecond = dataBase.readInt();
                        currentPosition = (int) dataBase.getFilePointer();
                    } else if (currentPosition == offsetOfValueFirst) {
                        offsetOfValueSecond = (int) dataBase.length();
                        ++currentPosition;
                    }
                    dataBase.seek(firstOffset);
                    int first = (int) dataBase.getFilePointer();
                    byte[] tmp;
                    if (offsetOfValueSecond != first) {
                        tmp = new byte[(int) (offsetOfValueSecond - first)];
                        dataBase.read(tmp);
                    } else {
                        tmp = new byte[(int) (dataBase.length() - offsetOfValueSecond)];
                        dataBase.read(tmp);
                    }
                    value = new String(tmp, StandardCharsets.UTF_8);
                    mapFile.put(keyFirst, value);
                    keyFirst = keySecond;
                    firstOffset = offsetOfValueSecond;
                } while (currentPosition < offsetOfValueFirst);
                if (keyFirst != null) {
                    int first = (int) dataBase.getFilePointer();
                    byte[] tmp;
                    if (offsetOfValueSecond != first) {
                        tmp = new byte[(int) (offsetOfValueSecond - first)];
                        dataBase.read(tmp);
                    } else {
                        tmp = new byte[(int) (dataBase.length() - offsetOfValueSecond)];
                        dataBase.read(tmp);
                    }
                    value = new String(tmp, StandardCharsets.UTF_8);
                    mapFile.put(keyFirst, value);
                }

            } else {
                throw new IOException("Offset is negative");
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage(), e);
        } finally {
            CloseFile.closeFile(dataBase);
        }
        return mapFile;
    }
}
