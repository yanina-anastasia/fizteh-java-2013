package ru.fizteh.fivt.students.inaumov.filemap;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import ru.fizteh.fivt.students.inaumov.filemap.AbstractTable;

public class WriteHandler implements Closeable {
    private RandomAccessFile outputFile = null;

    public WriteHandler(String fileName) throws IOException {
        try {
            outputFile = new RandomAccessFile(fileName, "rw");
        } catch (FileNotFoundException exception) {
            throw new IOException("can't create file " + fileName);
        }
        outputFile.setLength(0);
    }

    public static void saveToFile(String filePath, HashMap<String, String> data) throws IOException {
        WriteHandler writer = new WriteHandler(filePath);

        for (final Map.Entry<String, String> entry: data.entrySet()) {
            writer.writeEntry(entry.getKey(), entry.getValue());
        }

        try {
            writer.close();
        } catch (IOException exception) {

        }
    }

    public void writeEntry(String key, String value) throws IOException {
        outputFile.writeInt(key.length());
        outputFile.writeInt(value.length());

        outputFile.write(key.getBytes(AbstractTable.CHARSET));
        outputFile.write(value.getBytes(AbstractTable.CHARSET));
    }

    public void close() throws IOException {
        outputFile.close();
    }

}