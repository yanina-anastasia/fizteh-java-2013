package ru.fizteh.fivt.students.kinanAlsarmini.filemap;

import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

class TableReader {
    private FileInputStream inputStream;

    public TableReader(File databasePath) {
        try {
            inputStream = new FileInputStream(databasePath);
        } catch (IOException e) {
            throw new IllegalArgumentException("TableReader: file can't be opened for writing.");
        }
    }

    public static int convertByteArrayToInt(byte[] bytes) {
        return (bytes[3] & 0xFF)
            | (bytes[2] & 0xFF) << 8
            | (bytes[1] & 0xFF) << 16
            | (bytes[0] & 0xFF) << 24;
    }

    public void readTable(Table table) throws IOException {
        while (inputStream.available() > 0) {
            byte[] temp = new byte[4];
            int tRead = inputStream.read(temp);
            if (tRead < 0 || tRead != 4) {
                throw new IllegalArgumentException("TableReader: bad file format.");
            }
            int keyLength = convertByteArrayToInt(temp);

            tRead = inputStream.read(temp);
            if (tRead < 0 || tRead != 4) {
                throw new IllegalArgumentException("TableReader: bad file format.");
            }
            int valueLength = convertByteArrayToInt(temp);

            if (keyLength < 0 || valueLength < 0 || keyLength >= 10000 || valueLength >= 10000) {
                throw new IllegalArgumentException("TableReader: bad file format: invalid length for key / value.");
            }

            byte[] bKey = new byte[keyLength];
            tRead = inputStream.read(bKey);
            if (tRead < 0 || tRead != keyLength) {
                throw new IllegalArgumentException("TableReader: bad file format.");
            }
            String key = new String(bKey, "UTF-8");

            byte[] bValue = new byte[valueLength];
            tRead = inputStream.read(bValue);
            if (tRead < 0 || tRead != valueLength) {
                throw new IllegalArgumentException("TableReader: bad file format.");
            }
            String value = new String(bValue, "UTF-8");

            table.put(key, value);
        }
    }

    public void close() throws IOException {
        inputStream.close();
    }
}
