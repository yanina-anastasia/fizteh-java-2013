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

    public Code load(String pathInput) {
        pathDbFile = Paths.get(pathInput);
        pathDbFile = pathDbFile.resolve(name);
        try {
            dbFile = new RandomAccessFile(pathDbFile.toFile(), "rw");
        } catch (FileNotFoundException e) {
            System.err.println("File " + name + " is not found");
            return Code.ERROR;
        }
        dbStorage = new HashMap<>();
        long length = 0;
        try {
            length = dbFile.length();
        } catch (IOException e) {
            System.err.println(e);
            return Code.SYSTEM_ERROR;
        }
        if (length == 0) {
            return Code.OK;
        }
        long pointer = 0;
        try {
            pointer = dbFile.getFilePointer();
        } catch (IOException e) {
            System.err.println(e);
            return Code.SYSTEM_ERROR;
        }
        while (length > 0) {
            int lengthOfKey = 0;
            try {
                lengthOfKey = dbFile.readInt();
            } catch (IOException e) {
                System.err.println("Wrong format of db");
                return Code.ERROR;
            }
            length -= 4;
            int lengthOfValue;
            try {
                lengthOfValue = dbFile.readInt();
            } catch (IOException e) {
                System.err.println("Wrong format of db");
                return Code.ERROR;
            }
            length -= 4;
            byte[] bytesOfKey = new byte[lengthOfKey];
            int countOfBytesWasRead = 0;
            try {
                countOfBytesWasRead = dbFile.read(bytesOfKey);
            } catch (IOException e) {
                System.err.println("Wrong format of db");
                return Code.ERROR;
            }
            if (countOfBytesWasRead == -1) {
                System.err.println("Wrong format of db");
                return Code.ERROR;
            } else {
                length -= countOfBytesWasRead;
            }
            byte[] bytesOfValue = new byte[lengthOfValue];
            try {
                countOfBytesWasRead = dbFile.read(bytesOfValue);
            } catch (IOException e) {
                System.err.println("Wrong format of db");
                return Code.ERROR;
            }
            if (countOfBytesWasRead == -1) {
                System.err.println("Wrong format of db");
                return Code.ERROR;
            } else {
                length -= countOfBytesWasRead;
            }
            String key;
            try {
                key = new String(bytesOfKey, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.err.println(e);
                return Code.SYSTEM_ERROR;
            }
            String value;
            try {
                value = new String(bytesOfValue, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.err.println(e);
                return Code.SYSTEM_ERROR;
            }
            dbStorage.put(key, value);
        }
        return Code.OK;
    }

    public Code load() {
        String path = System.getProperty(pathToDataBaseDirectory);
        pathDbFile = Paths.get(path);
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
        long pointer = 0;
        try {
            pointer = dbFile.getFilePointer();
        } catch (IOException e) {
            System.err.println(e);
            return Code.SYSTEM_ERROR;
        }
        while (length > 0) {
            int lengthOfKey = 0;
            try {
                lengthOfKey = dbFile.readInt();
            } catch (IOException e) {
                System.err.println("Wrong format of db");
                return Code.ERROR;
            }
            length -= 4;
            int lengthOfValue;
            try {
                lengthOfValue = dbFile.readInt();
            } catch (IOException e) {
                System.err.println("Wrong format of db");
                return Code.ERROR;
            }
            length -= 4;
            byte[] bytesOfKey = new byte[lengthOfKey];
            int countOfBytesWasRead = 0;
            try {
                countOfBytesWasRead = dbFile.read(bytesOfKey);
            } catch (IOException e) {
                System.err.println("Wrong format of db");
                return Code.ERROR;
            }
            if (countOfBytesWasRead == -1) {
                System.err.println("Wrong format of db");
                return Code.ERROR;
            } else {
                length -= countOfBytesWasRead;
            }
            byte[] bytesOfValue = new byte[lengthOfValue];
            try {
                countOfBytesWasRead = dbFile.read(bytesOfValue);
            } catch (IOException e) {
                System.err.println("Wrong format of db");
                return Code.ERROR;
            }
            if (countOfBytesWasRead == -1) {
                System.err.println("Wrong format of db");
                return Code.ERROR;
            } else {
                length -= countOfBytesWasRead;
            }
            String key;
            try {
                key = new String(bytesOfKey, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.err.println(e);
                return Code.SYSTEM_ERROR;
            }
            String value;
            try {
                value = new String(bytesOfValue, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.err.println(e);
                return Code.SYSTEM_ERROR;
            }
            dbStorage.put(key, value);
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
