package ru.fizteh.fivt.students.annasavinova.filemap;

import ru.fizteh.fivt.students.annasavinova.shell.UserShell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileMap extends UserShell {
    private RandomAccessFile dataFile;
    private HashMap<String, String> dataMap;

    @Override
    public String[] getArgsFromString(String str) {
        str = str.trim();
        if (str != null) {
            return str.split("[\\s]+", 3);
        } else {
            return null;
        }
    }

    @Override
    public void printError(String errStr) {
        if (isPacket) {
            unloadFile();
            printErrorAndExit(errStr);
        } else {
            System.out.println(errStr);
        }
    }

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

    private void loadKeyAndValue() {
        try {
            int keyLong = dataFile.readInt();
            int valueLong = dataFile.readInt();
            byte[] keyBytes = new byte[keyLong];
            byte[] valueBytes = new byte[valueLong];
            dataFile.read(keyBytes);
            dataFile.read(valueBytes);
            String key = new String(keyBytes);
            String value = new String(valueBytes);
            dataMap.put(key, value);
        } catch (IOException e) {
            printErrorAndExit("Cannot load file");
        }
    }

    protected void loadFile(File data) {
        dataMap = new HashMap<>();
        setDataFile(data);
        try {
            dataFile.seek(0);
            while (dataFile.getFilePointer() != dataFile.length()) {
                loadKeyAndValue();
            }
        } catch (IOException e) {
            printErrorAndExit("Cannot load file");
        }
    }

    protected void unloadFile() {
        try {
            dataFile.setLength(0);
            Set<Map.Entry<String, String>> entries = dataMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                dataFile.writeInt(key.getBytes("UTF-8").length);
                dataFile.writeInt(value.getBytes("UTF-8").length);
                dataFile.write(key.getBytes("UTF-8"));
                dataFile.write(value.getBytes("UTF-8"));
            }
            dataFile.close();
            dataMap.clear();
        } catch (IOException e) {
            printErrorAndExit("Cannot unload file correctly");
        }
    }

    public void doPut(String key, String value) {
        if (dataMap.get(key) == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(dataMap.get(key));
        }
        dataMap.put(key, value);
    }

    public void doGet(String key) {
        if (dataMap.get(key) == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(dataMap.get(key));
        }
    }

    public void doRemove(String key) {
        if (dataMap.get(key) == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
            dataMap.remove(key);
        }
    }

    @Override
    protected void execProc(String[] args) {
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
                unloadFile();
                System.exit(0);
                break;
            default:
                printError("Unknown command");
            }
        }
    }

    public static void main(String[] args) {
        FileMap tmp = new FileMap();
        if (System.getProperty("fizteh.db.dir") == null) {
            tmp.printErrorAndExit("have no file");
        }
        File data = new File(System.getProperty("fizteh.db.dir") + File.separatorChar + "db.dat");
        tmp.loadFile(data);
        tmp.exec(args);
        tmp.unloadFile();
    }
}
