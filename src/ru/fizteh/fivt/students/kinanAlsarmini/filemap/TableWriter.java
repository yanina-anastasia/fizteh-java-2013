package ru.fizteh.fivt.students.kinanAlsarmini.filemap;

import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

class TableWriter {
    private FileOutputStream outputStream;

    public TableWriter(File databasePath) {
        try {
            outputStream = new FileOutputStream(databasePath);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("TableWriter: received non-existent file.");
        }
    }

    public static byte[] convertIntToByteArray(int n) {
        return ByteBuffer.allocate(4).putInt(n).array();
    }

    public void writeTable(Table table) throws IOException {
        Set<Map.Entry<String,String>> rows = table.listRows();

        for (Map.Entry<String,String> row : rows) {
            outputStream.write(convertIntToByteArray(row.getKey().length()));
            outputStream.write(convertIntToByteArray(row.getValue().length()));
            outputStream.write(row.getKey().getBytes("UTF-8"));
            outputStream.write(row.getValue().getBytes("UTF-8"));
        }
    }

    public void close() throws IOException {
        outputStream.close();
    }
}
