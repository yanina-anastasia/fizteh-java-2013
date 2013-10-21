package ru.fizteh.fivt.students.irinaGoltsman.filemap;

import ru.fizteh.fivt.students.irinaGoltsman.shell.*;

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

    public class LineOfDB {
        public long length = 0;
        public String key = "";
        public String value = "";

        public LineOfDB() {
        }

        public LineOfDB(long length, String key, String value) {
            this.length = length;
            this.key = key;
            this.value = value;
        }
    }

    public LineOfDB readLineOfDB(long inputLength) {
        long length = inputLength;
        int lengthOfKey;
        int lengthOfValue;
        LineOfDB empty = new LineOfDB();
        try {
            lengthOfKey = dbFile.readInt();
            lengthOfValue = dbFile.readInt();
        } catch (IOException e) {
            System.err.println("Wrong format of db");
            return empty;
        }
        length -= 8;
        if (lengthOfKey <= 0 || lengthOfValue <= 0) {
            System.err.println("Wrong format of db: length of key and length of value must be positive integers.");
            return empty;
        }
        if (lengthOfKey > length) {
            System.err.println("Wrong format of db: length of key ​​do not match content.");
            return empty;
        }
        byte[] bytesOfKey = readKeyOrValue(lengthOfKey);
        if (bytesOfKey.length == 0) {
            return empty;
        }
        length -= lengthOfKey;
        if (lengthOfValue > length) {
            System.err.println("Wrong format of db: length of value ​​do not match content.");
            return empty;
        }
        byte[] bytesOfValue = readKeyOrValue(lengthOfValue);
        if (bytesOfValue.length == 0) {
            return empty;
        }
        length -= lengthOfValue;
        String key;
        String value;
        try {
            key = new String(bytesOfKey, "UTF-8");
            value = new String(bytesOfValue, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.err.println(e);
            return empty;
        }
        long lengthOfLine = inputLength - length;
        LineOfDB result = new LineOfDB(lengthOfLine, key, value);
        return result;
    }

    public byte[] readKeyOrValue(int length) {
        byte[] bytes = new byte[length];
        int countOfBytesWasRead = 0;
        while (true) {
            try {
                countOfBytesWasRead = dbFile.read(bytes, countOfBytesWasRead, length - countOfBytesWasRead);
            } catch (IOException e) {
                System.err.println("Wrong format of db");
                bytes = new byte[0];
                return bytes;
            }
            if (countOfBytesWasRead == length) {
                break;
            }
            if (countOfBytesWasRead == -1) {
                System.err.println("Error while reading key or value");
                bytes = new byte[0];
                return bytes;
            }
        }
        return bytes;
    }

    public Code load() {
        String path = System.getProperty(pathToDataBaseDirectory);
        return (load(path));
    }

    public Code load(String inputPath) {
        if (inputPath == null) {
            System.err.println("Error with path to data base directory.");
            return Code.SYSTEM_ERROR;
        }
        pathDbFile = Paths.get(inputPath);
        pathDbFile = pathDbFile.resolve(name);
        try {
            dbFile = new RandomAccessFile(pathDbFile.toFile(), "rw");
        } catch (FileNotFoundException e) {
            System.err.println("File " + name + " is not found");
            return Code.ERROR;
        }
        dbStorage = new HashMap<>();
        long length = -1;
        try {
            length = dbFile.length();
        } catch (IOException e) {
            System.err.println(e);
            return Code.SYSTEM_ERROR;
        }
        if (length == 0) {
            return Code.OK;
        }
        while (length > 0) {
            LineOfDB line = readLineOfDB(length);
            if (line.length == 0) {
                return Code.ERROR;
            }
            length -= line.length;
            dbStorage.put(line.key, line.value);
        }
        return Code.OK;
    }

    public Code close() {
        try {
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
        } catch (Exception e) {
            System.err.println(e);
            try {
                dbFile.close();
            } catch (Exception e2) {
                System.err.println(e2);
            }
            return Code.SYSTEM_ERROR;
        }
        return Code.OK;
    }

    public Code emergencyExit() {
        try {
            dbFile.close();
        } catch (Exception e) {
            System.err.println(e);
            return Code.SYSTEM_ERROR;
        }
        return Code.OK;
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
        if (dbStorage.containsKey(key)) {
            System.out.println("overwrite");
            System.out.println(dbStorage.get(key));
        } else {
            System.out.println("new");
        }
        String value = args[2];
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
