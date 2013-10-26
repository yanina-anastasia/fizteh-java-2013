package ru.fizteh.fivt.students.lizaignatyeva.database;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;


public class Database {
    final File path;
    Map<String, String> data;
    public Database(String pathName) {
        path = new File(pathName);
        try {
            data = readFromFile(pathName);
        } catch (IOException e) {
            System.err.println("Error creating database: " + e.getMessage());
            System.exit(1);
        } catch (DataFormatException e) {
            System.err.println("Error creating database: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error creating database: " + e.getMessage());
            System.exit(1);
        }
    }

    private void readEntry(ByteBuffer buffer, Map<String, String> dest) throws BufferUnderflowException, DataFormatException {
        int keyLength = buffer.getInt();
        if (keyLength < 0) {
            throw new DataFormatException("too long key buffer");
        }
        int valueLength = buffer.getInt();
        if (valueLength < 0) {
            throw new DataFormatException("too long value buffer");
        }
        byte[] keyBytes = new byte[keyLength];
        buffer.get(keyBytes);
        byte[] valueBytes = new byte[valueLength];
        buffer.get(valueBytes);
        String key = new String(keyBytes, StandardCharsets.UTF_8);
        String value = new String(valueBytes, StandardCharsets.UTF_8);
        if (dest.containsKey(key)) {
            throw new DataFormatException("duplicating keys: " + key);
        }
        dest.put(key, value);
    }

    public HashMap<String, String> readFromFile(String fileName) throws IOException, DataFormatException {
        byte[] data = Files.readAllBytes(Paths.get(fileName));
        ByteBuffer buffer = ByteBuffer.wrap(data);
        HashMap<String, String> result = new HashMap<String, String>();
        try {
            while (buffer.hasRemaining()) {
                readEntry(buffer, result);
            }
        } catch (BufferUnderflowException e) {
            throw new DataFormatException("invalid file format");
        }
        return result;
    }

    private byte[] intToBytes(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    private void writeEntry(String key, String value, BufferedOutputStream outputStream) throws IOException {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
        outputStream.write(intToBytes(keyBytes.length));
        outputStream.write(intToBytes(valueBytes.length));
        outputStream.write(keyBytes);
        outputStream.write(valueBytes);
    }

    public void writeToFile() throws IOException {
        String fileName = path.getCanonicalPath();
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileName));
        for (String key: data.keySet()) {
            String value = data.get(key);
            writeEntry(key, value, outputStream);
        }
        outputStream.close();
    }

    public void write() {
        //it's a nice debugging tool i'd like to keep here
        System.out.println("we are off now");
        for (String str : data.keySet()) {
            System.out.println(str + " " + data.get(str));
        }

    }

}
