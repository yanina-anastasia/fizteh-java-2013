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
                closeFile(dataBase);
                System.exit(1);
            } catch (IOException e1) {
                System.err.println("Can't read from database");
                closeFile(dataBase);
                System.exit(1);
            }
            WorkWithInput input = new WorkWithInput();
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

    private static void readDatabase() throws IOException {
        char current = '0';
        StringBuffer keyFirst = new StringBuffer();
        StringBuffer keySecond = new StringBuffer();
        StringBuffer value = new StringBuffer();
        int currentPosition = (int) dataBase.getFilePointer();
        current = (char) dataBase.readByte();
        keyFirst = keyFirst.append(current);
        while (current != 0) {
            current = (char) dataBase.readByte();
            keyFirst = keyFirst.append(current);
        }
        dataBase.skipBytes(1);
        keyFirst = keyFirst.delete(keyFirst.length() - 1, keyFirst.length());
        current = '0';
        dataBase.seek((int) dataBase.getFilePointer() - 1);
        int offsetOfValueFirst = dataBase.readInt();
        if (offsetOfValueFirst > 0) {
            if (offsetOfValueFirst == ((int) dataBase.getFilePointer() + 1)) {
                currentPosition = offsetOfValueFirst;
                while (currentPosition != dataBase.length()) {
                    current = (char) dataBase.readByte();
                    value = value.append(current);
                    currentPosition = (int) dataBase.getFilePointer();
                }
                fileMap.put(keyFirst.toString(), value.toString());
                value.delete(0, value.length());
                keyFirst.delete(0, keyFirst.length());
            } else {
                final int endOfOffset = offsetOfValueFirst;
                int startOffset = offsetOfValueFirst;
                while (current != 0) {
                    current = (char) dataBase.readByte();
                    keySecond = keySecond.append(current);
                }
                dataBase.skipBytes(1);
                keySecond = keySecond.delete(keySecond.length() - 1, keySecond.length());
                current = '0';
                dataBase.seek((int) dataBase.getFilePointer() - 1);
                int offsetOfValueSecond = dataBase.readInt();
                currentPosition = (int) dataBase.getFilePointer();
                dataBase.seek(startOffset - 1);
                int currentPointer = (int) dataBase.getFilePointer();
                while (currentPointer != (offsetOfValueSecond - 1)) {
                    current = (char) dataBase.readByte();
                    value = value.append(current);
                    currentPointer = (int) dataBase.getFilePointer();
                }
                current = '0';
                dataBase.skipBytes(1);
                startOffset = offsetOfValueSecond;
                fileMap.put(keyFirst.toString(), value.toString());
                value.delete(0, value.length());
                keyFirst.delete(0, keyFirst.length());
                while (currentPosition != (endOfOffset - 1)) {
                    dataBase.seek(currentPosition);
                    while (current != 0) {
                        current = (char) dataBase.readByte();
                        keyFirst = keyFirst.append(current);
                    }
                    dataBase.skipBytes(1);
                    keyFirst = keyFirst.delete(keyFirst.length() - 1, keyFirst.length());
                    current = '0';
                    dataBase.seek((int) dataBase.getFilePointer() - 1);
                    offsetOfValueFirst = dataBase.readInt();
                    currentPosition = (int) dataBase.getFilePointer();
                    dataBase.seek(startOffset - 1);
                    while (currentPointer != (offsetOfValueFirst - 1)) {
                        current = (char) dataBase.readByte();
                        value = value.append(current);
                        currentPointer = (int) dataBase.getFilePointer();
                    }
                    current = '0';
                    startOffset = offsetOfValueFirst;
                    fileMap.put(keySecond.toString(), value.toString());
                    value.delete(0, value.length());
                    keySecond.delete(0, keySecond.length());
                    keySecond = keySecond.append(keyFirst);
                    keyFirst.delete(0, keyFirst.length());
                }
                dataBase.seek(offsetOfValueFirst - 1);
                while (currentPointer != dataBase.length()) {
                    current = (char) dataBase.readByte();
                    value = value.append(current);
                    currentPointer = (int) dataBase.getFilePointer();
                }
                fileMap.put(keySecond.toString(), value.toString());
                value.delete(0, value.length());
            }
        } else {
            System.err.println("Offset is negative");
            dataBase.close();
            System.exit(1);
        }
        dataBase.setLength(0);
        dataBase.close();
    }

    static void writeInDatabase() throws IOException {
        dataBase = new RandomAccessFile(workingDirectory + File.separator + "db.dat", "rw");
        int lengthOfkeys = 0;
        for (String key : fileMap.keySet()) {
            lengthOfkeys += (key.getBytes("UTF-8").length);
        }
        lengthOfkeys += (5 * fileMap.keySet().size());
        for (String key : fileMap.keySet()) {
            dataBase.write(key.getBytes("UTF-8"));
            dataBase.writeByte(0);
            dataBase.writeInt(lengthOfkeys + 1);
            lengthOfkeys += (fileMap.get(key).getBytes("UTF-8").length);
        }
        for (String key : fileMap.keySet()) {
            dataBase.write(fileMap.get(key).getBytes("UTF-8"));
        }
        dataBase.close();
    }
}
