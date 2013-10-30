package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.sterzhanovVladislav.shell.ShellUtility;



public class IOUtility {
    private static final int MAX_KEY_SIZE = 1 << 24;
    private static final int MAX_VALUE_SIZE = 1 << 24;

    public static HashMap<String, String> parseDatabase(Path dbDir) throws Exception {
        HashMap<String, String> map = new HashMap<String, String>();
        for (File subdir : dbDir.toFile().listFiles()) {
            if (!subdir.isDirectory() || !subdir.getName().matches("^([0-9]|[1][0-5])\\.dir$")) {
                throw new Exception("Malformed database");
            }
            for (File file : subdir.listFiles()) {
                if (!file.isFile() || !file.getName().matches("^([0-9]|[1][0-5])\\.dat$")) {
                    throw new Exception("Malformed database");
                }
                int dirID = Integer.parseInt(subdir.getName().replaceAll("\\.dir", ""));
                int fileID = Integer.parseInt(file.getName().replaceAll("\\.dat", ""));
                parseFileIntoDB(file, map, dirID, fileID);
            }
        }
        return map;
    }

    public static void parseFileIntoDB(File dbFile, HashMap<String, String> map, int checkDirID, int checkFileID) 
            throws FileNotFoundException, IOException, Exception {
        try (FileInputStream fstream = new FileInputStream(dbFile)) {
            while (fstream.available() > 0) {
                Map.Entry<String, String> newEntry = parseEntry(fstream, checkDirID, checkFileID);
                map.put(newEntry.getKey(), newEntry.getValue());
            }
        }
    }

    public static Map.Entry<String, String> parseEntry(FileInputStream fstream, int checkDirID, int checkFileID) 
            throws Exception {
        byte[] sizeBuf = new byte[4];
        safeRead(fstream, sizeBuf, 4);
        int keySize = ByteBuffer.wrap(sizeBuf).getInt();
        safeRead(fstream, sizeBuf, 4);
        int valueSize = ByteBuffer.wrap(sizeBuf).getInt();
        if (keySize <= 0 || valueSize <= 0 || keySize > MAX_KEY_SIZE || valueSize > MAX_VALUE_SIZE) {
            throw new Exception("Error: malformed database");
        }
        byte[] keyBuf = new byte[keySize];
        safeRead(fstream, keyBuf, keySize);
        byte[] valueBuf = new byte[valueSize];
        safeRead(fstream, valueBuf, valueSize);
        int hashcode = keyBuf.hashCode();
        int directoryID = hashcode % 16;
        if (directoryID < 0) {
            directoryID += 16;
        }
        int fileID = hashcode / 16 % 16;
        if (fileID < 0) {
            fileID += 16;
        }
        if (directoryID != checkDirID || fileID != checkFileID) {
            throw new Exception("Error: malformed database");
        }
        return new AbstractMap.SimpleEntry<String, String>(new String(keyBuf, "UTF-8"), new String(valueBuf, "UTF-8"));
    }

    public static void writeOut(HashMap<String, String> database, Path dir) throws IOException {
        try {
            ShellUtility.removeDir(dir);
        } catch (Exception e) {
            // Ignore
        }
        dir.toFile().mkdir();
        for (Map.Entry<String, String> entry : database.entrySet()) {
            int hashcode = entry.getKey().hashCode();
            int directoryID = hashcode % 16;
            if (directoryID < 0) {
                directoryID += 16;
            }
            int fileID = hashcode / 16 % 16;
            if (fileID < 0) {
                fileID += 16;
            }
            File subdir = Paths.get(dir.normalize() + "/" + directoryID + ".dir").toFile();
            if (!subdir.exists()) {
                subdir.mkdir();
            }
            File file = Paths.get(dir.normalize() + "/" + directoryID + ".dir/" + fileID + ".dat").toFile();
            if (!file.exists()) {
                file.createNewFile();
            }
            try (FileOutputStream fstream = new FileOutputStream(file, true)) {
                writeEntry(entry, fstream);
            }
        } 
    }

    public static void writeEntry(Map.Entry<String, String> e, FileOutputStream fstream) throws IOException {
        byte[] keyBuf = e.getKey().getBytes("UTF-8");
        byte[] valueBuf = e.getValue().getBytes("UTF-8");
        fstream.write(ByteBuffer.allocate(4).putInt(keyBuf.length).array());
        fstream.write(ByteBuffer.allocate(4).putInt(valueBuf.length).array());
        fstream.write(keyBuf);
        fstream.write(valueBuf);
    }

    public static void safeRead(FileInputStream fstream, byte[] buf, int readCount) throws Exception {
        int bytesRead = 0;
        if (readCount < 0) {
            throw new Exception("Error: malformed database");
        }
        while (bytesRead < readCount) {
            int readNow = fstream.read(buf, bytesRead, readCount - bytesRead);
            if (readNow < 0) {
                throw new Exception("Error: malformed database");
            }
            bytesRead += readNow;
        }
    }
}
