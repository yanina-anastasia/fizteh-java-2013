package ru.fizteh.fivt.students.kamilTalipov.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class InputStreamUtils {
    public static int readInt(FileInputStream input) throws IOException {
        byte[] buffer = new byte[Integer.SIZE / 8];
        int bytesRead = input.read(buffer);
        if (bytesRead != buffer.length) {
            throw new IOException("Couldn't read int");
        }

        ByteBuffer wrapped = ByteBuffer.wrap(buffer);
        return wrapped.getInt();
    }

    public static String readString(FileInputStream input, int length) throws IOException {
        byte[] buffer = new byte[length];
        int bytesRead = input.read(buffer);
        if (bytesRead != length) {
            throw new IOException("Couldn't read string");
        }

        return new String(buffer, "UTF-8");
    }
}
