package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class FileMap {
    public static String workingDirectory = System.getProperty("fizteh.db.dir");
    public static RandomAccessFile dataBase;
    public static FileMap[][] table = new FileMap[16][16];
    public HashMap<String, String> fileMap = new HashMap<String, String>();
    static boolean hasOpenedTable = false;
    public static String currentTable;

    public static void main(String[] args) throws FileNotFoundException {
        if (workingDirectory != null) {
            File directory = new File(workingDirectory);
            if (directory.isDirectory() && directory.exists()) {
                Commands cmd = new Commands();
                cmd.workWithShell(args);
                cmd.saveTable();
            } else {
                System.err.println("Not a directory");
                System.exit(1);
            }
        } else {
            System.err.println("No directory");
            System.exit(1);
        }
        return;
    }

    static void closeFile() {
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
        String result = new String(res, StandardCharsets.UTF_8);
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
        String result = new String(tmp, StandardCharsets.UTF_8);
        return result;
    }

    protected void readDatabase(File tmp) throws IOException {
        if (tmp.length() != 0) {
            fileMap = new HashMap<String, String>();
            dataBase = new RandomAccessFile(tmp.getAbsolutePath(), "rw");
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
                Commands cmd = new Commands();
                cmd.printError("Offset is negative");
                closeFile();
                System.exit(1);
            }
        }
    }

    protected void writeInDatabase(File tmp) throws IOException {
        dataBase = new RandomAccessFile(tmp.getAbsolutePath(), "rw");
        int lengthOfkeys = 0;
        Set<Map.Entry<String, String>> set = fileMap.entrySet();
        for (Map.Entry<String, String> entry : set) {
            lengthOfkeys += (entry.getKey().getBytes(StandardCharsets.UTF_8).length + 1 + 4);
        }
        dataBase.setLength(0);
        dataBase.seek(0);
        for (Map.Entry<String, String> myEntry : set) {
            dataBase.write(myEntry.getKey().getBytes(StandardCharsets.UTF_8));
            dataBase.writeByte(0);
            dataBase.writeInt(lengthOfkeys);
            lengthOfkeys += myEntry.getValue().getBytes(StandardCharsets.UTF_8).length;
        }
        for (Map.Entry<String, String> myEntry : set) {
            dataBase.write(myEntry.getValue().getBytes(StandardCharsets.UTF_8));
        }
        closeFile();
    }

    public void exec(String[] input) {
        Commands cmd = new Commands();
        switch (input[0]) {
        case "put":
            if (input.length == 3) {
                cmd.put(input[1], input[2]);
            } else {
                cmd.printError("Incorrect number of arguments");
            }
            break;
        case "get":
            if (input.length == 2) {
                cmd.get(input[1]);
            } else {
                cmd.printError("Incorrect number of arguments");
            }
            break;
        case "remove":
            if (input.length == 2) {
                cmd.remove(input[1]);
            } else {
                cmd.printError("Incorrect number of arguments");
            }
            break;
        default:
            cmd.printError("Incorrect input");
        }

    }
}
