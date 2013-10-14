package ru.fizteh.fivt.students.piakovenko.Dbmain.DataBase;


import ru.fizteh.fivt.students.piakovenko.Dbmain.MyException;

import java.io.*;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 12.10.13
 * Time: 22:45
 * To change this template use File | Settings | File Templates.
 */
public class DataBase {
    private static DataBase db = null;
    private static final String pathToDatabaseDirectory = "fizteh.db.dir";
    private static final String name = "db.dat";
    private static File dataBaseFile = null;
    private static RandomAccessFile raDataBaseFile = null;
    private static final DataBaseMap map = new DataBaseMap();

    private static void readFromFile() throws IOException{
        long length = raDataBaseFile.length();
        while (length > 0) {
            int l1 = raDataBaseFile.readInt();
            length -= 4;
            int l2 = raDataBaseFile.readInt();
            length -= 4;
            byte [] key = new byte [l1];
            byte [] value = new byte [l2];
            length -= raDataBaseFile.read(key);
            length -= raDataBaseFile.read(value);
            map.primaryPut(new String(key, "UTF_16"), new String(value, "UTF_16"));
        }
    }

    private static void saveToFile () throws IOException {
        long length  = 0;
        raDataBaseFile.seek(0);
        for (String key: map.getMap().keySet()) {
            int l1 = key.length();
            int l2 = map.getMap().get(key).length();
            raDataBaseFile.writeInt(2* l1);
            raDataBaseFile.writeInt(2* l2);
            raDataBaseFile.writeChars(key);
            raDataBaseFile.writeChars(map.getMap().get(key));
            length += 4 + 4 + 2* l1 + 2* l2;
        }
        raDataBaseFile.setLength(length);
    }


    public static void loadDataBase () throws IOException, MyException {
        File dataBaseDirectory = new File (System.getProperty(pathToDatabaseDirectory));
        if (!dataBaseDirectory.exists()) {
            throw new MyException(new Exception("DataBase directory " + dataBaseDirectory.getCanonicalPath() +
                                                           "doesn't exist!" ) );
        }
        dataBaseFile = new File (dataBaseDirectory, name);
        if (!dataBaseFile.exists()) {
            dataBaseFile.createNewFile();
            return;
        } else {
            raDataBaseFile = new RandomAccessFile(dataBaseFile, "rw");
            readFromFile();
        }
    }

    public static void saveDataBase () throws IOException, MyException {
        File dataBaseDirectory = new File (System.getProperty(pathToDatabaseDirectory));
        if (!dataBaseDirectory.exists()) {
            throw new MyException(new Exception("DataBase directory " + dataBaseDirectory.getCanonicalPath() +
                    "doesn't exist!" ) );
        }
        try {
            saveToFile();
        } finally {
            raDataBaseFile.close();
        }
    }


    public static DataBase getDataBase () {
        return db;
    }

    public static void get (String key) {
        map.get(key);
    }

    public static void put (String key, String value) {
        map.put(key, value);
    }

    public static void remove(String key) {
        map.remove(key);
    }

}
