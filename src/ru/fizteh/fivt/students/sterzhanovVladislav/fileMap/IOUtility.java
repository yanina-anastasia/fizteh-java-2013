package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class IOUtility {
    private static final int MAX_KEY_SIZE = 1 << 24;
    private static final int MAX_VALUE_SIZE = 1 << 24;

    public static FileMap parseDatabase(Path dbDir) 
            throws IllegalStateException, IOException {
        HashMap<String, String> map = new HashMap<String, String>();
        if (!dbDir.toFile().exists() || !dbDir.toFile().isDirectory()) {
            throw new IOException("directory does not exist");
        }
        for (File subdir : dbDir.toFile().listFiles()) {
            if (!subdir.isDirectory() || !subdir.getName().matches("^([0-9]|[1][0-5])\\.dir$")) {
                throw new IllegalStateException("Malformed database");
            }
            for (File file : subdir.listFiles()) {
                if (!file.isFile() || !file.getName().matches("^([0-9]|[1][0-5])\\.dat$")) {
                    throw new IllegalStateException("Malformed database");
                }
                int dirID = Integer.parseInt(subdir.getName().replaceAll("\\.dir", ""));
                int fileID = Integer.parseInt(file.getName().replaceAll("\\.dat", ""));
                parseFileIntoDB(file, map, dirID, fileID);
            }
        }
        return new FileMap(dbDir.toFile().getName(), map);
    }

    public static void parseFileIntoDB(File dbFile, HashMap<String, String> map, int checkDirID, int checkFileID) 
            throws FileNotFoundException, IOException {
        try (FileInputStream fstream = new FileInputStream(dbFile)) {
            while (fstream.available() > 0) {
                Map.Entry<String, String> newEntry = parseEntry(fstream, checkDirID, checkFileID);
                map.put(newEntry.getKey(), newEntry.getValue());
            }
        }
    }

    public static Map.Entry<String, String> parseEntry(FileInputStream fstream, int checkDirID, int checkFileID) 
            throws IllegalStateException, IOException, UnsupportedEncodingException {
        byte[] sizeBuf = new byte[4];
        safeRead(fstream, sizeBuf, 4);
        int keySize = ByteBuffer.wrap(sizeBuf).getInt();
        safeRead(fstream, sizeBuf, 4);
        int valueSize = ByteBuffer.wrap(sizeBuf).getInt();
        if (keySize <= 0 || valueSize <= 0 || keySize > MAX_KEY_SIZE || valueSize > MAX_VALUE_SIZE) {
            throw new IllegalStateException("Error: malformed database");
        }
        byte[] keyBuf = new byte[keySize];
        safeRead(fstream, keyBuf, keySize);
        byte[] valueBuf = new byte[valueSize];
        safeRead(fstream, valueBuf, valueSize);
        int b = keyBuf[0];
        if (b < 0) {
            b *= -1;
        }
        int directoryID = b % 16;
        int fileID = b / 16 % 16;
        if (directoryID != checkDirID || fileID != checkFileID) {
            throw new IllegalStateException("Error: malformed database");
        }
        return new AbstractMap.SimpleEntry<String, String>(new String(keyBuf, StandardCharsets.UTF_8), 
                new String(valueBuf, "UTF-8"));
    }

    public static void writeEntry(Map.Entry<String, String> e, FileOutputStream fstream) throws IOException {
        byte[] keyBuf = e.getKey().getBytes("UTF-8");
        byte[] valueBuf = e.getValue().getBytes("UTF-8");
        fstream.write(ByteBuffer.allocate(4).putInt(keyBuf.length).array());
        fstream.write(ByteBuffer.allocate(4).putInt(valueBuf.length).array());
        fstream.write(keyBuf);
        fstream.write(valueBuf);
    }

    public static void safeRead(FileInputStream fstream, byte[] buf, int readCount) 
            throws IllegalStateException, IOException {
        int bytesRead = 0;
        if (readCount < 0) {
            throw new IllegalStateException("Error: malformed database");
        }
        while (bytesRead < readCount) {
            int readNow = fstream.read(buf, bytesRead, readCount - bytesRead);
            if (readNow < 0) {
                throw new IllegalStateException("Error: malformed database");
            }
            bytesRead += readNow;
        }
    }
}
