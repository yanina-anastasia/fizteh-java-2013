package ru.fizteh.fivt.students.irinaGoltsman.filemap;

import java.io.*;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class DataBase {
    private final String pathToDataBaseDirectory = "fizteh.db.dir";
    private final String name = "db.dat";
    static RandomAccessFile dbFile = null;
    static HashMap<String, String> dbStorage = new HashMap<>();  //1 - ключ, 2 - значение
    static Path pathDbFile = null;

    public void load(String pathInput) throws Exception {
        pathDbFile = Paths.get(pathInput);
        pathDbFile = pathDbFile.resolve(name);
        dbFile = new RandomAccessFile(pathDbFile.toFile(), "rw");
        dbStorage = new HashMap<>();

        if (dbFile.length() == 0) {
            return;
        }
        long pointer = dbFile.getFilePointer();
        String key = "";
        String value = "";
        long length = dbFile.length();
        while (length > 0) {
            int lengthOfKey = dbFile.readInt();
            length -= 4;
            int lengthOfValue = dbFile.readInt();
            length -= 4;
            byte[] bytesOfKey = new byte[lengthOfKey];
            int countOfBytesWasRead = dbFile.read(bytesOfKey);
            if (countOfBytesWasRead == -1) {
                throw (new Exception("Wrong format of db"));
            } else {
                length -= countOfBytesWasRead;
            }
            byte[] bytesOfValue = new byte[lengthOfValue];
            countOfBytesWasRead = dbFile.read(bytesOfValue);
            if (countOfBytesWasRead == -1) {
                throw (new Exception("Wrong format of db"));
            } else {
                length -= countOfBytesWasRead;
            }
            dbStorage.put(new String(bytesOfKey, "UTF-8"), new String(bytesOfValue, "UTF-8"));
        }
    }

    public void load() throws Exception {
        String path = System.getProperty(pathToDataBaseDirectory);
        pathDbFile = Paths.get(path);
        pathDbFile = pathDbFile.resolve(name);
        dbFile = new RandomAccessFile(pathDbFile.toFile(), "rw");
        dbStorage = new HashMap<>();

        if (dbFile.length() == 0) {
            return;
        }
        long pointer = dbFile.getFilePointer();
        String key = "";
        String value = "";
        long length = dbFile.length();
        while (length > 0) {
            int lengthOfKey = dbFile.readInt();
            length -= 4;
            int lengthOfValue = dbFile.readInt();
            length -= 4;
            byte[] bytesOfKey = new byte[lengthOfKey];
            int countOfBytesWasRead = dbFile.read(bytesOfKey);
            if (countOfBytesWasRead == -1) {
                throw (new Exception("Wrong format of db"));
            } else {
                length -= countOfBytesWasRead;
            }
            byte[] bytesOfValue = new byte[lengthOfValue];
            countOfBytesWasRead = dbFile.read(bytesOfValue);
            if (countOfBytesWasRead == -1) {
                throw (new Exception("Wrong format of db"));
            } else {
                length -= countOfBytesWasRead;
            }
            dbStorage.put(new String(bytesOfKey, "UTF-8"), new String(bytesOfValue, "UTF-8"));
        }
    }

    public void close() throws Exception {
        dbFile.seek(0);
        dbFile.setLength(0);
        for (String key : dbStorage.keySet()) {
            byte[] bytesOfKey = key.getBytes("UTF-8");
            byte[] bytesOfValue = dbStorage.get(key).getBytes("UTF-8");
            dbFile.writeInt(bytesOfKey.length);
            dbFile.writeInt(bytesOfValue.length);
            dbFile.write(bytesOfKey);
            dbFile.write(bytesOfValue);
        }
        dbFile.close();
    }

    public void emergencyExit() throws Exception {
        dbFile.seek(0);
        dbFile.close();
    }

    public static Code get(String[] args) {
        String key = args[1];
        if (dbStorage.containsKey(key)) {
            System.out.println("found");
            System.out.println(dbStorage.get(key));
        } else {
            System.out.println("not found");
        }
        return Code.OK;
    }

    public static Code put(String[] args) {
        String key = args[1];
        String value = args[2];
        if (dbStorage.containsKey(key)) {
            System.out.println("overwrite");
            System.out.println(dbStorage.get(key));
        } else {
            System.out.println("new");
        }
        dbStorage.put(key, value);
        return Code.OK;
    }

    public static Code remove(String[] args) {
        String key = args[1];
        if (dbStorage.containsKey(key)) {
            dbStorage.remove(key);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        return Code.OK;
    }
}
