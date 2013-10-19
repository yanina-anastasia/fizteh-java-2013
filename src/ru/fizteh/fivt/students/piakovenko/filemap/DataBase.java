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
    private final String pathToDatabaseDirectory = "fizteh.db.dir";
    private final String name = "db.dat";
    private File dataBaseFile = null;
    private RandomAccessFile raDataBaseFile = null;
    private DataBaseMap map = null;
    private Shell shell = null;

    private void readFromFile() throws IOException, MyException {
        long length = raDataBaseFile.length();
        while (length > 0) {
            int l1 = raDataBaseFile.readInt();
            if (l1 <= 0) {
                throw new MyException(new Exception("Length of new key less or equals zero"));
            } else if (l1 > 1024 * 1024) {
                throw new MyException(new Exception("Key greater than 1 MB"));
            }
            length -= 4;
            int l2 = raDataBaseFile.readInt();
            if (l2 <= 0) {
                throw new MyException(new Exception("Length of new value less or equals zero"));
            } else if (l2 > 1024 * 1024) {
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
            map.primaryPut(new String(key, StandardCharsets.UTF_8), new String(value, StandardCharsets.UTF_8));
        }
    }

    private void saveToFile () throws IOException {
        long length  = 0;
        raDataBaseFile.seek(0);
        for (String key: map.getMap().keySet()) {
            byte [] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte [] valueBytes = map.getMap().get(key).getBytes(StandardCharsets.UTF_8);
            raDataBaseFile.writeInt(keyBytes.length);
            raDataBaseFile.writeInt(valueBytes.length);
            raDataBaseFile.write(keyBytes);
            raDataBaseFile.write(valueBytes);
            length += 4 + 4 + keyBytes.length + valueBytes.length;
        }
        raDataBaseFile.setLength(length);
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
            raDataBaseFile = new RandomAccessFile(dataBaseFile, "rw");
            return;
        } else {
            raDataBaseFile = new RandomAccessFile(dataBaseFile, "rw");
            try {
                readFromFile();
            } catch (MyException e) {
                System.err.println("Error! " + e.what());
                System.exit(1);
            }
        }
    }

    public DataBase (Shell sl){
        map = new DataBaseMap();
        shell  = sl;
        shell.changeInvitation("DataBase $ ");
    }

    public void initialize () {
        shell.addCommand(new Exit(this));
        shell.addCommand(new Put(this));
        shell.addCommand(new Get(this));
        shell.addCommand(new Remove(this));
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



    public void saveDataBase () throws IOException, MyException {
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

    public void get (String key) {
        map.get(key);
    }

    public void put (String key, String value) {
        map.put(key, value);
    }

    public void remove(String key) {
        map.remove(key);
    }

}
