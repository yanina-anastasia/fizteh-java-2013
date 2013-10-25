package ru.fizteh.fivt.students.adanilyak.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 1:08
 */
public class WorkWithDatFiles {
    public static void readIntoMap(File dataBaseFile, Map<String, String> map) throws IOException {
        RandomAccessFile dataBaseFileReader = new RandomAccessFile(dataBaseFile, "rw");
        long lenght = dataBaseFile.length();
        byte[] buffer;

        while (lenght > 0) {
            int keyLenght = dataBaseFileReader.readInt();
            lenght -= 4;
            int valueLenght = dataBaseFileReader.readInt();
            lenght -= 4;

            buffer = new byte[keyLenght];
            dataBaseFileReader.readFully(buffer);
            lenght -= buffer.length;
            String key = new String(buffer, StandardCharsets.UTF_8);

            buffer = new byte[valueLenght];
            dataBaseFileReader.readFully(buffer);
            lenght -= buffer.length;
            String value = new String(buffer, StandardCharsets.UTF_8);

            map.put(key, value);
        }
        dataBaseFileReader.close();
    }

    public static void writeIntoFile(File dataBaseFile, Map<String, String> map) throws IOException {
        RandomAccessFile dataBaseFileWriter = new RandomAccessFile(dataBaseFile, "rw");
        try {
            dataBaseFileWriter.setLength(0);
            for (Map.Entry<String, String> element : map.entrySet()) {
                String key = element.getKey();
                byte[] bufferKey = key.getBytes(StandardCharsets.UTF_8);
                dataBaseFileWriter.writeInt(bufferKey.length);

                String value = element.getValue();
                byte[] bufferValue = value.getBytes(StandardCharsets.UTF_8);
                dataBaseFileWriter.writeInt(bufferValue.length);

                dataBaseFileWriter.write(bufferKey);
                dataBaseFileWriter.write(bufferValue);
            }
            dataBaseFileWriter.close();
        } catch (IOException exc) {
            System.err.println(exc.getMessage());
            dataBaseFileWriter.close();
        }
    }
}
