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

    private String byteToString(byte[] symbols) {
        StringBuilder sb = new StringBuilder();
        for (byte symbol : symbols) {
            sb.append((char) symbol);
        }
        return sb.toString();
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
            byte[] keySymbols = new byte[keyLength];
            byte[] valueSymbols = new byte[valueLength];
            database.read(keySymbols);
            database.read(valueSymbols);
            key = byteToString(keySymbols);
            value = byteToString(valueSymbols);
            if (key.equals(targetKey)) {
                return value;
            }
        } while (database.getFilePointer() != database.length());
        return "";
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