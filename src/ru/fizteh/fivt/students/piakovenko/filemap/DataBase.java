package ru.fizteh.fivt.students.piakovenko.filemap;



import ru.fizteh.fivt.students.piakovenko.shell.MyException;
import ru.fizteh.fivt.students.piakovenko.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

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
    private static DataBaseMap map = null;
    private static Shell shell = null;

    private void readFromFile() throws IOException, MyException {
        long length = raDataBaseFile.length();
        while (length > 0) {
            int l1 = raDataBaseFile.readInt();
            if (l1 <= 0) {
                throw new MyException(new Exception("Length of new key less or equals zero"));
            } else if (l1 > 1024) {
                throw new MyException(new Exception("Key greater than 1 MB"));
            }
            length -= 4;
            int l2 = raDataBaseFile.readInt();
            if (l2 <= 0) {
                throw new MyException(new Exception("Length of new value less or equals zero"));
            } else if (l2 > 1024) {
                throw new MyException(new Exception("Value greater than 1 MB"));
            }
            length -= 4;
            byte [] key = new byte [l1];
            byte [] value = new byte [l2];
            if (raDataBaseFile.read(key) < l1) {
                throw new MyException(new Exception("Key: read less, that it was pointed to read"));
            } else {
                length -= l1;
            }
            if (raDataBaseFile.read(value) < l2) {
                throw new MyException(new Exception("Value: read less, that it was pointed to read"));
            } else {
                length -= l2;
            }
            map.primaryPut(new String(key, StandardCharsets.UTF_16), new String(value, StandardCharsets.UTF_16));
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

    private Boolean checkDataBase () {
      return true;
    }

    private void loadDataBase () throws IOException, MyException {
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
            if (!checkDataBase()){
                throw new MyException(new Exception("DataBase file was spoiled!"));
            }
            raDataBaseFile = new RandomAccessFile(dataBaseFile, "rw");
            readFromFile();
        }
    }

    public DataBase (Shell sl){
        map = new DataBaseMap();
        shell  = sl;
        shell.changeInvitation("DataBase $ ");
    }

    public void initialize () {
        shell.addCommand(new Exit());
        shell.addCommand(new Put());
        shell.addCommand(new Get());
        shell.addCommand(new Remove());
        try {
            loadDataBase();
        } catch (IOException e) {
            System.err.println("Error! " + e.getMessage());
            System.exit(1);
        } catch (MyException e) {
            System.err.println("Error! " + e.what());
            System.exit(1);
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
