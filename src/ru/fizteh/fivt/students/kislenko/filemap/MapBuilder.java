package ru.fizteh.fivt.students.kislenko.filemap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MapBuilder {
    private final long MAX_FILE_SIZE = 100000000;

    void buildMap(State state) throws IOException {
        File checker = state.getPath().toFile();
        if (!checker.exists()) {
            throw new FileNotFoundException("File not found.");
        }
        RandomAccessFile database = new RandomAccessFile(state.getPath().toFile(), "r");
        int keyLength;
        int valueLength;
        String key;
        String value;
        while (database.getFilePointer() != database.length()) {
            if (database.getFilePointer() > MAX_FILE_SIZE) {
                throw new IOException("Too big database file.");
            }
            keyLength = database.readInt();
            valueLength = database.readInt();
            byte[] keySymbols = new byte[keyLength];
            byte[] valueSymbols = new byte[valueLength];
            database.read(keySymbols);
            database.read(valueSymbols);
            key = new String(keySymbols, StandardCharsets.UTF_8);
            value = new String(valueSymbols, StandardCharsets.UTF_8);
            state.putValue(key, value);
        }
        database.close();
    }

    void fillFile(State state) throws IOException {
        RandomAccessFile database = new RandomAccessFile(state.getPath().toFile(), "rw");
        database.setLength(0);
        Set<String> keySet = state.getMap().keySet();
        for (String key : keySet) {
            if (database.getFilePointer() > MAX_FILE_SIZE) {
                throw new IOException("Too big database file.");
            }
            database.writeInt(key.getBytes(StandardCharsets.UTF_8).length);
            database.writeInt(state.getValue(key).getBytes(StandardCharsets.UTF_8).length);
            database.write(key.getBytes(StandardCharsets.UTF_8));
            database.write(state.getValue(key).getBytes(StandardCharsets.UTF_8));
        }
        database.close();
    }
}