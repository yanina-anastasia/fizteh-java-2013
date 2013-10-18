package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class FileMap {
    public static String workingDirectory = System.getProperty("fizteh.db.dir");
    public static RandomAccessFile dataBase;
    static HashMap<String, String> fileMap = new HashMap<String, String>();

    public static void main(String[] args) throws FileNotFoundException {
        File directory = new File(workingDirectory);
        if (directory.isDirectory()) {
            dataBase = new RandomAccessFile(workingDirectory + File.separator + "db.dat", "rw");
            try {
                if (dataBase.length() != 0) {
                    readDatabase();
                }
            } catch (FileNotFoundException e) {
                System.err.println("Can't read database");
                System.exit(1);
            } catch (IOException e1) {
                System.err.println("Can't read from database");
                System.exit(1);
            }
            WorkWithInput.check(args);
            try {
                writeInDatabase();
            } catch (FileNotFoundException e) {
                System.err.println("Can't read database");
                System.exit(1);
            } catch (IOException e1) {
                System.err.println("Can't write in database");
                System.exit(1);
            }
            try {
                dataBase.close();
            } catch (IOException e2) {
                System.err.println("Can't close a database");
                System.exit(1);
            }
        } else {
            System.err.println("Not a directory");
            System.exit(1);
        }
        return;
    }

    private static void readDatabase() throws IOException {
        String key;
        String value;
        boolean firstUse = true;
        int currentPosition = (int) dataBase.getFilePointer();
        int offsetOfValueFirst = 0;
        while (currentPosition < offsetOfValueFirst || firstUse) {
            dataBase.seek(currentPosition);
            key = dataBase.readUTF();
            dataBase.readByte();
            int offsetOfValueSecond = dataBase.readInt();
            if (firstUse) {
                offsetOfValueFirst = offsetOfValueSecond;
                firstUse = false;
            }
            currentPosition = (int) dataBase.getFilePointer();
            dataBase.seek(offsetOfValueSecond);
            value = dataBase.readUTF();
            fileMap.put(key, value);
        }
        dataBase.setLength(0);
        dataBase.close();
    }

    static void writeInDatabase() throws IOException {
        dataBase = new RandomAccessFile(workingDirectory + File.separator + "db.dat", "rw");
        int lengthOfkeys = 0;
        for (String key : fileMap.keySet()) {
            lengthOfkeys += (key.getBytes().length + 2);
        }
        lengthOfkeys += (5 * fileMap.keySet().size());
        for (String key : fileMap.keySet()) {
            dataBase.writeUTF(key);
            dataBase.writeByte(0);
            dataBase.writeInt(lengthOfkeys);
            lengthOfkeys += (fileMap.get(key).getBytes("UTF-8").length + 2);
        }
        for (String key : fileMap.keySet()) {
            dataBase.writeUTF(fileMap.get(key));
        }
        dataBase.close();
    }
}
