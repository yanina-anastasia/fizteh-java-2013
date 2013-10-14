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

    private String byteToString(byte[] symbols) {
        StringBuilder sb = new StringBuilder();
        for (byte symbol : symbols) {
            sb.append((char) symbol);
        }
        return sb.toString();
    }

    private byte[] stringToByte(String s) throws IOException {
        byte[] b = new byte[s.length()];
        for (int i = 0; i < b.length; ++i) {
            if (s.charAt(i) > 255) {
                throw new IOException("put: Not in UTF-8.");
            }
            b[i] = (byte) s.charAt(i);
        }
        return b;
    }

    private byte[] intToByte(int a) {
        byte[] result = new byte[4];
        result[0] = (byte) ((a >> 12) & 15);
        result[1] = (byte) ((a >> 8) & 15);
        result[2] = (byte) ((a >> 4) & 15);
        result[3] = (byte) (a & 15);
        return result;
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

    private void put(String key, String value) throws IOException {
        database.seek(database.length());
        byte[] keySize = intToByte(key.length());
        byte[] valueSize = intToByte(value.length());
        database.write(keySize);
        database.write(valueSize);
        database.write(stringToByte(key));
        database.write(stringToByte(value));
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