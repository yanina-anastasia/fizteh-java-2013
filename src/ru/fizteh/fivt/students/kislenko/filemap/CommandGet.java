package ru.fizteh.fivt.students.kislenko.filemap;

import java.io.IOException;
import java.io.RandomAccessFile;

public class CommandGet implements Command {
    RandomAccessFile database;

    public String getName() {
        return "get";
    }

    public int getArgCount() {
        return 1;
    }

    private String get(String targetKey) throws IOException {
        if (database.length() == 0) {
            return "";
        }
        int keyLength;
        int valueLength;
        String key;
        String value;
        do {
            keyLength = database.readInt();
            valueLength = database.readInt();
            key = "";
            value = "";
            for (int i = 0; i < keyLength; ++i) {
                key = key + database.readChar();
            }
            for (int i = 0; i < valueLength; ++i) {
                value = value + database.readChar();
            }
        } while (!key.equals(targetKey) && database.getFilePointer() != database.length());
        if (key.equals(targetKey)) {
            return value;
        } else {
            return "";
        }
    }

    public void run(State state, String[] args) throws IOException {
        database = new RandomAccessFile(state.getState().toFile(), "r");
        String value = get(args[0]);
        if (value.equals("")) {
            System.out.println("not found");
        } else {
            System.out.println("found\n" + value);
        }
        database.close();
    }
}