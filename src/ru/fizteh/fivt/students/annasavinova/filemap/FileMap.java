package ru.fizteh.fivt.students.annasavinova.filemap;

import ru.fizteh.fivt.students.annasavinova.shell.UserShell;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileMap extends UserShell {
    protected HashMap<String, String> dataMap;
    private static String rootDir = System.getProperty("fizteh.db.dir") + File.separatorChar;
    private String currTable = "";
    private boolean hasLoadedData = false;

    protected File getDirWithNum(int dirNum) {
        File res = new File(rootDir + currTable + File.separatorChar + dirNum + ".dir");
        return res;
    }

    protected File getFileWithNum(int fileNum, int dirNum) {
        File res = new File(rootDir + currTable + File.separatorChar + dirNum + ".dir" + File.separatorChar + fileNum
                + ".dat");
        return res;
    }

    protected void loadData() {
        if (!hasLoadedData) {
            for (int i = 0; i < 16; ++i) {
                File currentDir = getDirWithNum(i);
                if (!currentDir.exists()) {
                    if (!currentDir.mkdir()) {
                        printErrorAndExit("Cannot create new directory");
                    }
                } else if (!currentDir.isDirectory()) {
                    printErrorAndExit("Incorrect files in table");
                }
                for (int j = 0; j < 16; ++j) {
                    File currentFile = getFileWithNum(j, i);
                    if (!currentFile.exists()) {
                        try {
                            currentFile.createNewFile();
                        } catch (IOException e) {
                            printErrorAndExit("Cannot create new file");
                        }
                    }
                    loadFile(currentFile, j, i);
                }
            }
            hasLoadedData = true;
        }
    }

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
            unloadData();
            printErrorAndExit(errStr);
        } else {
            System.out.println(errStr);
        }
    }

    protected void printErrorAndExit(String errMessage) {
        System.err.println(errMessage);
        System.exit(1);
    }

    public String appendArgs(int num, String[] args) {
        StringBuffer str = new StringBuffer(args[num]);
        for (int i = num + 1; i < args.length; ++i) {
            str.append(" ");
            str.append(args[i]);
        }
        return str.toString();
    }

    protected void loadKeyAndValue(RandomAccessFile dataFile, int nfile, int ndir) {
        try {
            int keyLong = dataFile.readInt();
            int valueLong = dataFile.readInt();
            if (keyLong <= 0 || valueLong <= 0) {
                dataFile.close();
                printErrorAndExit("Cannot Load File");
            } else {
                byte[] keyBytes = new byte[keyLong];
                byte[] valueBytes = new byte[valueLong];
                dataFile.read(keyBytes);
                dataFile.read(valueBytes);
                String key = new String(keyBytes);
                String value = new String(valueBytes);
                byte b = 0;
                b = (byte) Math.abs(keyBytes[0]);
                int directoryNum = b % 16;
                int fileNum = b / 16 % 16;
                if (nfile != fileNum || directoryNum != ndir) {
                    try {
                        dataFile.close();
                    } catch (IOException e1) {
                        printErrorAndExit("Cannot load file");
                    }
                    printErrorAndExit("Incorrect input");
                }
                dataMap.put(key, value);
            }
        } catch (IOException | OutOfMemoryError e) {
            try {
                dataFile.close();
            } catch (IOException e1) {
                printErrorAndExit("Cannot load file");
            }
            printErrorAndExit("Cannot load file");
        }
    }

    protected void loadFile(File data, int nfile, int ndir) {
        dataMap = new HashMap<>();
        RandomAccessFile dataFile = null;
        try {
            dataFile = new RandomAccessFile(data, "rw");
            dataFile.seek(0);
            while (dataFile.getFilePointer() != dataFile.length()) {
                loadKeyAndValue(dataFile, nfile, ndir);
            }
        } catch (IOException e) {
            printErrorAndExit("Cannot load file1");
        } finally {
            try {
                dataFile.close();
            } catch (IOException e1) {
                printErrorAndExit("Cannot load file2");
            }
        }
    }

    protected void doDelete(File currFile) {
        if (currFile.exists()) {
            if (!currFile.isDirectory() || currFile.listFiles().length == 0) {
                if (!currFile.delete()) {
                    printErrorAndExit("Cannot remove file");
                }
            } else {
                while (currFile.listFiles().length != 0) {
                    doDelete(currFile.listFiles()[0]);
                }
                if (!currFile.delete()) {
                    printErrorAndExit("Cannot remove file");
                }
            }
        }
    }

    protected void unloadMap() {
        RandomAccessFile[] filesArray = new RandomAccessFile[256];
        try {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    filesArray[i * 16 + j] = new RandomAccessFile(getFileWithNum(j, i), "rw");
                    filesArray[i * 16 + j].setLength(0);
                }
            }
            Set<Map.Entry<String, String>> entries = dataMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                byte[] keyBytes = key.getBytes("UTF-8");
                byte[] valueBytes = value.getBytes("UTF-8");
                byte b = 0;
                b = (byte) Math.abs(keyBytes[0]);
                int ndirectory = b % 16;
                int nfile = b / 16 % 16;
                filesArray[ndirectory * 16 + nfile].writeInt(keyBytes.length);
                filesArray[ndirectory * 16 + nfile].writeInt(valueBytes.length);
                filesArray[ndirectory * 16 + nfile].write(keyBytes);
                filesArray[ndirectory * 16 + nfile].write(valueBytes);
            }
        } catch (IOException e) {
            printErrorAndExit("Cannot unload file correctly");
        } finally {
            dataMap.clear();
            try {
                for (int i = 0; i < 16; ++i) {
                    for (int j = 0; j < 16; ++j) {
                        if (filesArray[i * 16 + j].length() == 0) {
                            filesArray[i * 16 + j].close();
                            doDelete(getFileWithNum(j, i));
                        } else {
                            filesArray[i * 16 + j].close();
                        }
                    }
                }
            } catch (IOException e) {
                printErrorAndExit("Cannot unload file");
            }
        }
    }

    protected void unloadData() {
        if (hasLoadedData) {
            unloadMap();
            for (int i = 0; i < 16; ++i) {
                File currentDir = getDirWithNum(i);
                if (currentDir.list().length == 0) {
                    doDelete(currentDir);
                }
                hasLoadedData = false;
            }
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

    public void doCreateTable(String tableName) {
        File tableDir = new File(rootDir + tableName);
        if (tableDir.exists()) {
            System.out.println(tableName + " exists");
        } else {
            if (!tableDir.mkdir()) {
                printErrorAndExit("Cannot create new directory");
            }
            System.out.println("created");
        }
    }

    public void doDropTable(String tableName) {
        File tableDir = new File(rootDir + tableName);
        if (!tableDir.exists()) {
            System.out.println(tableName + " not exists");
        } else {
            doDelete(tableDir);
            System.out.println("dropped");
        }
    }

    public void doUseTable(String tableName) {
        File tableDir = new File(rootDir + tableName);
        if (!tableDir.exists()) {
            System.out.println(tableName + " not exists");
        } else {
            unloadData();
            currTable = tableName;
            loadData();
            System.out.println("using " + tableName);
        }
    }

    @Override
    protected void execProc(String[] args) {
        if (args != null && args.length != 0) {
            switch (args[0]) {
            case "create":
                if (args.length > 1) {
                    doCreateTable(appendArgs(1, args));
                } else {
                    printError("Incorrect number of args");
                }
                break;
            case "drop":
                if (args.length > 1) {
                    doDropTable(appendArgs(1, args));
                } else {
                    printError("Incorrect number of args");
                }
                break;
            case "use":
                if (args.length > 1) {
                    doUseTable(appendArgs(1, args));
                } else {
                    printError("Incorrect number of args");
                }
                break;
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
                unloadData();
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
        File root = new File(rootDir);
        if (rootDir == null || !root.exists() || !root.isDirectory()) {
            tmp.printErrorAndExit("Incorrect root directory");
        }
        tmp.exec(args);
        tmp.unloadData();
    }
}
