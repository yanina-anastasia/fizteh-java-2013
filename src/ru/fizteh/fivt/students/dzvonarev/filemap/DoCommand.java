package ru.fizteh.fivt.students.dzvonarev.filemap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DoCommand {

    public DoCommand(String fileName) {
        try {
            readFileMap(fileName);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static HashMap<String, String> fileMap;

    public static HashMap<String, String> getFileMap() {
        return fileMap;
    }

    public static void closeFile(RandomAccessFile file) throws IOException {
        try {
            file.close();
        } catch (IOException e) {
            throw new IOException("error in closing file");
        }
    }

    public static void updateFile(String fileName) throws IOException {
        if (fileMap == null) {
            return;
        } else {
            if (fileMap.isEmpty()) {
                return;
            }
        }
        RandomAccessFile fileWriter = openFileForWrite(fileName);
        Set fileSet = fileMap.entrySet();
        Iterator<Map.Entry<String, String>> i = fileSet.iterator();
        try {
            while (i.hasNext()) {
                Map.Entry<String, String> currItem = i.next();
                String key = currItem.getKey();
                String value = currItem.getValue();
                if (key == null || value == null) {
                    throw new IOException("updating file: error in writing");
                }
                byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
                byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
                fileWriter.writeInt(keyBytes.length);
                fileWriter.writeInt(valueBytes.length);
                fileWriter.write(keyBytes);
                fileWriter.write(valueBytes);
            }
        } catch (IOException e) {
            throw new IOException("updating file: error in writing");
        } finally {
            closeFile(fileWriter);
        }
    }

    public static void readFileMap(String fileName) throws IOException {
        fileMap = new HashMap<String, String>();
        RandomAccessFile fileReader = openFileForRead(fileName);
        long endOfFile = fileReader.length();
        long currFilePosition = fileReader.getFilePointer();
        while (currFilePosition != endOfFile) {
            int keyLen = fileReader.readInt();
            int valueLen = fileReader.readInt();
            if (keyLen <= 0 || valueLen <= 0) {
                closeFile(fileReader);
                System.out.println(fileName + " : file is broken");
                System.exit(1);
            }
            byte[] keyByte = null;
            byte[] valueByte = null;
            try {
                keyByte = new byte[keyLen];
                valueByte = new byte[valueLen];
            } catch (OutOfMemoryError e) {
                closeFile(fileReader);
                System.out.println(fileName + " : file is broken");
                System.exit(1);
            }
            fileReader.readFully(keyByte, 0, keyLen);
            fileReader.readFully(valueByte, 0, valueLen);
            String key = new String(keyByte);
            String value = new String(valueByte);
            fileMap.put(key, value);
            currFilePosition = fileReader.getFilePointer();
            endOfFile = fileReader.length();
        }
        closeFile(fileReader);
    }

    public static RandomAccessFile openFileForRead(String fileName) throws IOException {
        RandomAccessFile newFile;
        try {
            newFile = new RandomAccessFile(fileName, "rw");
        } catch (FileNotFoundException e) {
            throw new IOException("reading from file: file not found");
        }
        if (newFile == null) {
            throw new IOException("reading from file: file not found");
        }
        return newFile;
    }

    public static RandomAccessFile openFileForWrite(String fileName) throws IOException {
        RandomAccessFile newFile;
        try {
            newFile = new RandomAccessFile(fileName, "rw");
            newFile.getChannel().truncate(0);
        } catch (FileNotFoundException e) {
            throw new IOException("writing to file: file not found");
        }
        if (newFile == null) {
            throw new IOException("writing to file: file not found");
        }
        return newFile;
    }

    public static String getRealPath() throws IOException {
        if (!System.getProperties().containsKey("fizteh.db.dir")) {
            throw new IOException("wrong properties");
        }
        String realPath;
        String dirName = System.getProperty("fizteh.db.dir");
        File destFile = new File(dirName + File.separator + "db.dat");
        if (!destFile.exists()) {
            if (new File(System.getProperty("fizteh.db.dir")).getName().equals("db.dat")) {
                realPath = System.getProperty("fizteh.db.dir");
            } else {
                if (!destFile.createNewFile()) {
                    throw new IOException("can't create db.dat file");
                }
                realPath = dirName + File.separator + "db.dat";
            }
        } else {
            realPath = dirName + File.separator + "db.dat";
        }
        return realPath;
    }

}
