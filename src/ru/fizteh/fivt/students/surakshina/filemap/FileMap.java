package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

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
                closeFile(dataBase);
                System.exit(1);
            } catch (IOException e1) {
                System.err.println("Can't read from database");
                closeFile(dataBase);
                System.exit(1);
            }
            Commands input = new Commands();
            input.workWithShell(args);
            try {
                writeInDatabase();
            } catch (FileNotFoundException e) {
                System.err.println("Can't read database");
                closeFile(dataBase);
                System.exit(1);
            } catch (IOException e1) {
                System.err.println("Can't write in database");
                closeFile(dataBase);
                System.exit(1);
            }
            closeFile(dataBase);
        } else {
            System.err.println("Not a directory");
            System.exit(1);
        }
        return;
    }

    static void closeFile(RandomAccessFile file) {
        try {
            dataBase.close();
        } catch (IOException e2) {
            System.err.println("Can't close a database");
            System.exit(1);
        }
    }

    public static String getKey() throws IOException {
        byte c = 0;
        Vector<Byte> vector = new Vector<Byte>();
        c = dataBase.readByte();
        while (c != 0) {
            vector.add(c);
            c = dataBase.readByte();
        }
        byte[] res = new byte[vector.size()];
        for (int i = 0; i < vector.size(); i++) {
            res[i] = vector.elementAt(i).byteValue();
        }
        String result = new String(res, "UTF-8");
        return result;
    }

    public static String getValue(int offsetOfValueSecond) throws IOException {
        int first = (int) dataBase.getFilePointer();
        byte[] tmp;
        if (offsetOfValueSecond != first) {
            tmp = new byte[(int) (offsetOfValueSecond - first)];
            dataBase.read(tmp);
        } else {
            tmp = new byte[(int) (dataBase.length() - offsetOfValueSecond)];
            dataBase.read(tmp);
        }
        String result = new String(tmp, "UTF-8");
        return result;
    }

    private static void readDatabase() throws IOException {
        String keyFirst;
        String keySecond = null;
        String value;
        int currentPosition = (int) dataBase.getFilePointer();
        int offsetOfValueFirst = 0;
        int firstOffset = 0;
        int offsetOfValueSecond = 0;
        dataBase.seek(currentPosition);
        keyFirst = getKey();
        offsetOfValueFirst = dataBase.readInt();
        if (offsetOfValueFirst > 0) {
            currentPosition = (int) dataBase.getFilePointer();
            firstOffset = offsetOfValueFirst;
            do {
                dataBase.seek(currentPosition);
                if (currentPosition < firstOffset) {
                    keySecond = getKey();
                    offsetOfValueSecond = dataBase.readInt();
                    currentPosition = (int) dataBase.getFilePointer();
                } else if (currentPosition == offsetOfValueFirst) {
                    offsetOfValueSecond = (int) dataBase.length();
                    ++currentPosition;
                }
                dataBase.seek(firstOffset);
                value = getValue(offsetOfValueSecond);
                fileMap.put(keyFirst, value);
                keyFirst = keySecond;
                firstOffset = offsetOfValueSecond;
            } while (currentPosition < offsetOfValueFirst);
            if (keyFirst != null) {
                value = getValue(offsetOfValueSecond);
                fileMap.put(keyFirst, value);
            }

        } else {
            System.err.println("Offset is negative");
            closeFile(dataBase);
            System.exit(1);
        }
    }

    static void writeInDatabase() throws IOException {
        dataBase = new RandomAccessFile(workingDirectory + File.separator + "db.dat", "rw");
        int lengthOfkeys = 0;
        Set<Map.Entry<String, String>> set = fileMap.entrySet();
        for (Map.Entry<String, String> entry : set) {
            lengthOfkeys += (entry.getKey().getBytes("UTF-8").length + 1 + 4);
        }
        dataBase.setLength(0);
        dataBase.seek(0);
        for (Map.Entry<String, String> myEntry : set) {
            dataBase.write(myEntry.getKey().getBytes("UTF-8"));
            dataBase.writeByte(0);
            dataBase.writeInt(lengthOfkeys);
            lengthOfkeys += myEntry.getValue().getBytes("UTF-8").length;
        }
        for (Map.Entry<String, String> myEntry : set) {
            dataBase.write(myEntry.getValue().getBytes("UTF-8"));
        }
        closeFile(dataBase);
    }
}
