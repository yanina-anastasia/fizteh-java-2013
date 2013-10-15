package ru.fizteh.fivt.students.kislenko.filemap;

import java.io.*;
import java.util.*;

public class MapBuilder {
    void buildMap(State state) throws IOException {
        RandomAccessFile database = new RandomAccessFile(state.getPath().toFile(), "rw");
        int keyLength;
        int valueLength;
        String key;
        String value;
        while (database.getFilePointer() != database.length()) {
            keyLength = database.readInt();
            valueLength = database.readInt();
            byte[] keySymbols = new byte[keyLength];
            byte[] valueSymbols = new byte[valueLength];
            database.read(keySymbols);
            database.read(valueSymbols);
            key = new String(keySymbols, "UTF-8");
            value = new String(valueSymbols, "UTF-8");
            state.putValue(key, value);
        }
    }

    void fillFile(State state) throws IOException {
        RandomAccessFile database = new RandomAccessFile(state.getPath().toFile(), "rw");
        database.setLength(0);
        Set<String> keySet = state.getMap().keySet();
        for (String key : keySet) {
            database.writeInt(key.length());
            database.writeInt(state.getValue(key).length());
            database.write(key.getBytes("UTF-8"));
            database.write(state.getValue(key).getBytes("UTF-8"));
        }
    }
}