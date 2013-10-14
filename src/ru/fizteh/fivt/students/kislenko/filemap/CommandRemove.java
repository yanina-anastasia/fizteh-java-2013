package ru.fizteh.fivt.students.kislenko.filemap;

import java.io.IOException;
import java.io.RandomAccessFile;

public class CommandRemove implements Command {
    RandomAccessFile database;
    long position;
    int length;

    public String getName() {
        return "remove";
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

    private void find(String targetKey) throws IOException {
        if (database.length() == 0) {
            position = -1;
            return;
        }
        int keyLength;
        int valueLength;
        String key;
        do {
            keyLength = database.readInt();
            valueLength = database.readInt();
            byte[] keySymbols = new byte[keyLength];
            byte[] valueSymbols = new byte[valueLength];
            database.read(keySymbols);
            database.read(valueSymbols);
            key = byteToString(keySymbols);
            byteToString(valueSymbols);
            if (key.equals(targetKey)) {
                length = keyLength + valueLength + 8;
                position = database.getFilePointer() - length;
                return;
            }
        } while (database.getFilePointer() != database.length());
        position = -1;
    }

    private void remove() throws IOException {
        byte[] part = new byte[(int) (database.length() - (position + length))];
        database.seek(position + length);
        database.read(part);
        database.seek(position);
        database.write(part);
        database.setLength(database.length() - length);
    }

    public void run(State state, String[] args) throws IOException {
        database = new RandomAccessFile(state.getState().toFile(), "rw");
        find(args[0]);
        if (position != -1) {
            remove();
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        database.close();
    }
}