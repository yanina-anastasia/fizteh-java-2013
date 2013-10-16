package ru.fizteh.fivt.students.annasavinova.filemap;

import ru.fizteh.fivt.students.annasavinova.shell.UserShell;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileMap extends UserShell {
    private static RandomAccessFile dataFile;

    private static String getKey(long pointer) {
        try {
            dataFile.seek(pointer);
            int keyLong = dataFile.readInt();
            int valueLong = dataFile.readInt();
            byte[] byteArray = new byte[keyLong];
            dataFile.read(byteArray, 0, keyLong);
            String key = new String(byteArray, "UTF-16");
            dataFile.skipBytes(valueLong);
            return key;
        } catch (IOException e) {
            System.err.println("Can't read key");
            System.exit(1);
        }
        return "PANIC_KEY";
    }

    private static String getValue(long pointer) {
        try {
            dataFile.seek(pointer);
            int keyLong = dataFile.readInt();
            int valueLong = dataFile.readInt();
            byte[] byteArray = new byte[valueLong];
            dataFile.skipBytes(keyLong);
            dataFile.read(byteArray, 0, valueLong);
            String value = new String(byteArray, "UTF-16");
            return value;
        } catch (IOException e) {
            System.err.println("Can't read value");
            System.exit(1);
        }
        return "PANIC_VALUE";
    }

    private static long findKey(String key) {
        try {
            dataFile.seek(0);
            long currPointer = dataFile.getFilePointer();
            while (currPointer != dataFile.length()) {
                if (key.equals(getKey(currPointer))) {
                    return currPointer;
                }
                currPointer = dataFile.getFilePointer();
            }
        } catch (IOException e) {
            System.err.println("Can't find key");
            System.exit(1);
        }
        return -1;
    }

    public static void doPut(String key, String value) {
        try {
            long pointToKey = findKey(key);
            if (dataFile.length() == 0 || pointToKey == -1) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(getValue(pointToKey));
                delete(key);
            }
            dataFile.seek(dataFile.length());
            dataFile.writeInt(2 * key.length());
            dataFile.writeInt(2 * value.length());
            dataFile.writeChars(key);
            dataFile.writeChars(value);
        } catch (IOException e) {
            System.err.println("Can't put");
            System.exit(1);
        }
    }

    public static void doGet(String key) {
        long findRes = findKey(key);
        try {
            if (findRes == -1 || dataFile.length() == 0) {
                System.out.println("not found");
            } else {
                String value = getValue(findRes);
                System.out.println("found");
                System.out.println(value);
            }
        } catch (IOException e) {
            System.err.println("Can't get");
            System.exit(1);
        }
    }

    private static void copy(RandomAccessFile source, RandomAccessFile dest, long offset, long length) {
        int tmp = (int) length;
        byte[] arr = new byte[tmp];
        try {
            source.seek(offset);
            source.read(arr, 0, tmp);
            dest.seek(dest.length());
            dest.write(arr);
        } catch (IOException e) {
            System.err.println("Can't rewrite file");
            System.exit(1);
        }

    }

    private static void delete(String key) {
        try {
            long keyPointer = findKey(key);
            File tmp = File.createTempFile("DataBase", key);
            RandomAccessFile tmpFile = new RandomAccessFile(tmp, "rw");
            copy(dataFile, tmpFile, 0, keyPointer);
            dataFile.seek(keyPointer);
            int keyLong = dataFile.readInt();
            int valueLong = dataFile.readInt();
            long recordEnd = dataFile.getFilePointer() + keyLong + valueLong;
            copy(dataFile, tmpFile, recordEnd, dataFile.length() - recordEnd);
            dataFile.setLength(0);
            copy(tmpFile, dataFile, 0, tmpFile.length());
            dataFile.setLength(tmpFile.length());
            tmpFile.close();
        } catch (IOException e) {
            System.err.println("Can't remove");
            System.exit(1);
        }
    }

    public static void doRemove(String key) {
        long keyPointer = findKey(key);
        if (keyPointer == -1) {
            UserShell.printError("not found");
        } else {
            delete(key);
            System.out.println("removed");
        }
    }

    @Override
    protected void execProc(String[] args) {
        try {
            dataFile = new RandomAccessFile(System.getProperty("fizteh.db.dir") + "db.dat", "rw");
            if (args != null && args.length != 0) {
                switch (args[0]) {
                case "put":
                    if (args.length > 3) {
                        StringBuffer str = new StringBuffer(args[2]);
                        for (int i = 3; i < args.length; ++i) {
                            str.append(" ");
                            str.append(args[i]);
                        }
                        doPut(args[1], str.toString());
                    } else {
                        if (UserShell.checkArgs(3, args)) {
                            doPut(args[1], args[2]);
                        }
                    }
                    break;
                case "get":
                    if (UserShell.checkArgs(2, args)) {
                        doGet(args[1]);
                    }
                    break;
                case "remove":
                    if (UserShell.checkArgs(2, args)) {
                        doRemove(args[1]);
                    }
                    break;
                case "exit":
                    dataFile.close();
                    System.exit(0);
                    break;
                default:
                    printError("Unknown command");
                }
                dataFile.close();
            }
        } catch (IOException e) {
            System.err.println("Cannot open or close file");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        FileMap tmp = new FileMap();
        tmp.exec(args);
    }
}
