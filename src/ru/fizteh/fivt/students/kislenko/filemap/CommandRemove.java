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