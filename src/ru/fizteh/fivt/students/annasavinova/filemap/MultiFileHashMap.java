package ru.fizteh.fivt.students.annasavinova.filemap;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Set;

public class MultiFileHashMap extends FileMap {
    private String currTable = "";
    private static String rootDir = System.getProperty("fizteh.db.dir") + File.separatorChar;
    private FileMap[] dataArray = new FileMap[256];
    private boolean hasLoadingMaps = false;

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
                b = key.getBytes("UTF-8")[0];
            } catch (UnsupportedEncodingException e) {
                System.err.println("Cannot get hashcode");
            }
            if (b < 0) {
                b = (byte) -b;
            }
            if (b % 16 != dirNum || b / 16 % 16 != fileNum) {
                printErrorAndExit("Incorrect input files");
            }
        }
    }

    protected void loadData() {
        if (!hasLoadingMaps) {
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
                File currentDir = getDirWithNum(i);
                if (currentDir.list().length == 0) {
                    doDelete(currentDir);
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
                                b = args[1].getBytes("UTF-8")[0];
                            } catch (UnsupportedEncodingException e) {
                                System.err.println("Cannot get hashcode");
                            }
                            if (b < 0) {
                                b = (byte) -b;
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
            tmp.printErrorAndExit("Incorrect root directory");
        }
        tmp.exec(args);
        tmp.unloadData();
    }
}
