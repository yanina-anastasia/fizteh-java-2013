package ru.fizteh.fivt.students.annasavinova.filemap;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Set;

import ru.fizteh.fivt.students.annasavinova.shell.UserShell;

public class MultiFileHashMap extends UserShell {
    private String currTable = "";
    private static String rootDir = System.getProperty("fizteh.db.dir") + File.separatorChar;
    private FileMap[] dataArray = new FileMap[256];
    private boolean hasLoadingMaps = false;
    
    @Override
    public String[] getArgsFromString(String str) {
        str = str.trim();
        if (str != null) {
            return str.split("[\\s]+", 3);
        } else {
            return null;
        }
    }

    protected File getDirWithNum(int dirNum) {
        File res = new File(rootDir + currTable + File.separatorChar + dirNum + ".dir");
        return res;
    }

    protected File getFileWithNum(int fileNum, int dirNum) {
        File res = new File(rootDir + currTable + File.separatorChar + dirNum + ".dir" + File.separatorChar + fileNum
                + ".dat");
        return res;
    }

    private void checkKeysInMap(HashMap<String, String> map, int fileNum, int dirNum) {
        Set<String> keys = map.keySet();
        for (String key : keys) {
            byte b = 0;
            try {
                b = (byte) Math.abs(key.getBytes("UTF-8")[0]);
            } catch (UnsupportedEncodingException e) {
                System.err.println("Cannot get hashcode");
            }
            int ndirectory = b % 16;
            int nfile = b / 16 % 16;
            if (ndirectory != dirNum || nfile != fileNum) {
                FileMap.printErrorAndExit("Incorrect input files");
            }
        }
    }

    protected void loadData() {
        if (!hasLoadingMaps) {
            for (int i = 0; i < 16; ++i) {
                File currentDir = getDirWithNum(i);
                if (!currentDir.exists()) {
                    if (!currentDir.mkdir()) {
                        FileMap.printErrorAndExit("Cannot create new directory");
                    }
                } else if (!currentDir.isDirectory()) {
                    FileMap.printErrorAndExit("Incorrect files in table");
                }
                for (int j = 0; j < 16; ++j) {
                    File currentFile = getFileWithNum(j, i);
                    if (!currentFile.exists()) {
                        try {
                            currentFile.createNewFile();
                        } catch (IOException e) {
                            FileMap.printErrorAndExit("Cannot create new file");
                        }
                    }
                    if (dataArray[i * 16 + j] == null) {
                        dataArray[i * 16 + j] = new FileMap();
                    }
                    dataArray[i * 16 + j].loadFile(currentFile);
                    checkKeysInMap(dataArray[i * 16 + j].dataMap, j, i);
                }
            }
            hasLoadingMaps = true;
        }
    }

    protected void unloadData() {
        if (hasLoadingMaps) {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    dataArray[i * 16 + j].unloadFile();
                }
            }
            hasLoadingMaps = false;
        }
    }

    public void doCreateTable(String tableName) {
        File tableDir = new File(rootDir + tableName);
        if (tableDir.exists()) {
            System.out.println(tableName + " exists");
        } else {
            if (!tableDir.mkdir()) {
                FileMap.printErrorAndExit("Cannot create new directory");
            }
            System.out.println("created");
        }
    }

    public void doDropTable(String tableName) {
        File tableDir = new File(rootDir + tableName);
        if (!tableDir.exists()) {
            System.out.println(tableName + " not exists");
        } else {
            if (!tableDir.delete()) {
                FileMap.printErrorAndExit("Cannot delete");
            }
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
                    doCreateTable(FileMap.appendArgs(1, args));
                } else {
                    printError("Incorrect number of args");
                }
                break;
            case "drop":
                if (args.length > 1) {
                    doDropTable(FileMap.appendArgs(1, args));
                } else {
                    printError("Incorrect number of args");
                }
                break;
            case "use":
                if (args.length > 1) {
                    doUseTable(FileMap.appendArgs(1, args));
                } else {
                    printError("Incorrect number of args");
                }
                break;
            case "exit":
                unloadData();
                System.exit(0);
                break;
            default:
                if (args[0].equals("put") || args[0].equals("get") || args[0].equals("remove")) {
                    if (args.length > 1) {

                        if (currTable.equals("")) {
                            System.out.println("no table");
                        } else {
                            byte b = 0;
                            try {
                                b = (byte) Math.abs(args[1].getBytes("UTF-8")[0]);
                            } catch (UnsupportedEncodingException e) {
                                System.err.println("Cannot get hashcode");
                            }
                            int ndirectory = b % 16;
                            int nfile = b / 16 % 16;
                            dataArray[ndirectory * 16 + nfile].execProc(args);
                        }
                    } else {
                        printError("Incorrect number of args");
                    }
                } else {
                    printError("Unknown command");
                }
            }
        }
    }

    public static void main(String[] args) {
        MultiFileHashMap tmp = new MultiFileHashMap();
        File root = new File(rootDir);
        if (rootDir == null || !root.exists() || !root.isDirectory()) {
            System.err.println("Incorrect root directory");
            System.exit(1);
        }
        tmp.exec(args);
        tmp.unloadData();
    }
}
