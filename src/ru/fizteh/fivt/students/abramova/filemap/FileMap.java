package ru.fizteh.fivt.students.abramova.filemap;

import java.io.*;
import java.util.*;

public class FileMap implements Closeable {
    protected String fileMapName;
    protected Map<String, String> fileMap;

    public FileMap(String name, String directory) throws IOException {
        fileMapName = name;
        if (directory != null) {
            fileMapName = new File(directory, fileMapName).getCanonicalPath();
        }
        BufferedInputStream reader;
        try {
            File dbDat = new File(fileMapName);
            if (!dbDat.exists()) {
                dbDat.createNewFile();
            }
            reader = new BufferedInputStream(new FileInputStream(fileMapName));
        } catch (IOException e) {
            throw new IOException("Opening error: " + e.getMessage());
        }
        fileMap = new HashMap<String, String>();
        reading(fileMap, reader);
        try {
            reader.close();
        } catch (IOException e) {
            throw new IOException("Close error: " + e.getMessage());
        }
    }

    private void reading(Map<String, String> fileMap, BufferedInputStream reader) throws IOException {
        int keyLen;
        int valueLen;
        byte[] key;
        byte[] value;
        try {
            while ((keyLen = reader.read()) != -1) {
                valueLen = reader.read();
                if (keyLen + valueLen > reader.available()) {
                    throw new IOException("Bad file");
                }
                key = new byte[keyLen];
                value = new byte[valueLen];
                reader.read(key, 0, keyLen);
                reader.read(value, 0, valueLen);
                fileMap.put(new String(key), new String(value));
            }
        } catch (IOException e) {
            throw new IOException("Read error: " + e.getMessage());
        }
    }

    public void close() throws IOException {
        BufferedOutputStream writer;
        try {
            writer = new BufferedOutputStream(new FileOutputStream(fileMapName));
        } catch (IOException e) {
            throw new IOException("Open error: " + e.getMessage());
        }
        writing(fileMap, writer);
        try {
            writer.close();
        } catch (IOException e) {
            throw new IOException("Close error: " + e.getMessage());
        }
    }

    private void writing(Map<String, String> fileMap, BufferedOutputStream writer) throws IOException {
        int keyLen;
        int valueLen;
        byte[] key;
        byte[] value;
        Set<String> keys = fileMap.keySet();
        for (String keyString : keys) {
            key = keyString.getBytes();
            value = fileMap.get(keyString).getBytes();
            keyLen = key.length;
            valueLen = value.length;
            try {
                writer.write(keyLen);
                writer.write(valueLen);
                writer.write(key);
                writer.write(value);
            } catch (IOException e) {
                throw new IOException("Write error: " + e.getMessage());
            }
        }
        try {
            writer.flush();
        } catch (IOException e) {
            throw new IOException("Flush error: " + e.getMessage());
        }
    }

    public Map<String, String> getMap() {
        return fileMap;
    }
}
