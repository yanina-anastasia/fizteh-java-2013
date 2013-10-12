package ru.fizteh.fivt.students.kislenko.filemap;

import java.io.IOException;
import java.io.RandomAccessFile;

public class CommandPut implements Command {
    RandomAccessFile database;
    long position;
    int length;

    public String getName() {
        return "put";
    }

    public int getArgCount() {
        return 2;
    }

    private void find(String targetKey) throws IOException {
        if (database.length() == 0) {
            position = -1;
            return;
        }
        int keyLength = database.readInt();
        int valueLength = database.readInt();
        String key;
        do {
            key = "";
            for (int i = 0; i < keyLength; ++i) {
                key = key + database.readChar();
            }
            for (int i = 0; i < valueLength; ++i) {
                database.readChar();
            }
        } while (!key.equals(targetKey) && database.getFilePointer() != database.length());
        if (key.equals(targetKey)) {
            length = 2 * (keyLength + valueLength) + 8;
            position = database.getFilePointer() - length;
        } else {
            position = -1;
        }
    }

    private void remove() throws IOException {
        byte[] part = new byte[(int) (database.length() - (position + length))];
        database.seek(position + length);
        database.read(part);
        database.seek(position);
        database.write(part);
        database.setLength(database.length() - length);
    }

    private byte[] intToByte(int a) {
        byte[] result = new byte[4];
        result[0] = (byte) ((a >> 12) & 15);
        result[1] = (byte) ((a >> 8) & 15);
        result[2] = (byte) ((a >> 4) & 15);
        result[3] = (byte) (a & 15);
        return result;
    }

    private void put(String key, String value) throws IOException {
        database.seek(database.length());
        byte[] keySize = intToByte(key.length());
        byte[] valueSize = intToByte(value.length());
        database.write(keySize);
        database.write(valueSize);
        database.writeChars(key);
        database.writeChars(value);
    }

    public void run(State state, String[] args) throws IOException {
        database = new RandomAccessFile(state.getState().toFile(), "rw");
        find(args[0]);
        if (position != -1) {
            remove();
            put(args[0], args[1]);
            System.out.println("overwrite\n" + args[1]);
        } else {
            put(args[0], args[1]);
            System.out.println("new");
        }
        database.close();
    }
}