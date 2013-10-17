package ru.fizteh.fivt.students.annasavinova.filemap;

import ru.fizteh.fivt.students.annasavinova.shell.UserShell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileMap extends UserShell {
    private RandomAccessFile dataFile;

    protected void printErrorAndExit(String errMessage) {
        System.err.println(errMessage);
        System.exit(1);
    }

    public void setDataFile(File file) {
        try {
            dataFile = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            printErrorAndExit("Cannot open file");
        }
    }

    public String appendArgs(int num, String[] args) {
        StringBuffer str = new StringBuffer(args[num]);
        for (int i = num + 1; i < args.length; ++i) {
            str.append(" ");
            str.append(args[i]);
        }
        return str.toString();
    }

    private String getKey(long pointer) {
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
            printErrorAndExit("Can't read file");
        }
        return "PANIC_KEY";
    }

    private String getValue(long pointer) {
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
            printErrorAndExit("Can't read file");
        }
        return "PANIC_VALUE";
    }

    private long findKey(String key) {
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
            printErrorAndExit("Can't read file");
        }
        return -1;
    }

    public void doPut(String key, String value) {
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
            printErrorAndExit("Can't put");
        }
    }

    public void doGet(String key) {
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
            printErrorAndExit("Can't get");
        }
    }

    private void copy(RandomAccessFile source, RandomAccessFile dest, long offset, long length) {
        int tmp = (int) length;
        byte[] arr = new byte[tmp];
        try {
            source.seek(offset);
            source.read(arr, 0, tmp);
            dest.seek(dest.length());
            dest.write(arr);
        } catch (IOException e) {
            printErrorAndExit("Can't rewrite file");
        }

    }

    private void delete(String key) {
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
            printErrorAndExit("Can't remove");
        }
    }

    public void doRemove(String key) {
        long keyPointer = findKey(key);
        if (keyPointer == -1) {
            System.out.println("not found");
        } else {
            delete(key);
            System.out.println("removed");
        }
    }

    @Override
    protected void execProc(String[] args) {
        try {
            File data = new File(System.getProperty("fizteh.db.dir") + File.separatorChar + "db.dat");
            setDataFile(data);
            if (args != null && args.length != 0) {
                switch (args[0]) {
                case "put":
                    if (args.length > 2) {
                        doPut(args[1], appendArgs(2, args));
                    } else {
                        printError("Incorrect number of args");
                    }
                    break;
                case "get":
                    if (checkArgs(2, args)) {
                        doGet(args[1]);
                    }
                    break;
                case "remove":
                    if (checkArgs(2, args)) {
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
            printErrorAndExit("Cannot open or close file");
        }
    }

    public static void main(String[] args) {
        FileMap tmp = new FileMap();
        tmp.exec(args);
    }
}
