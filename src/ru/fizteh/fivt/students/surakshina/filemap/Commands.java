package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;

import ru.fizteh.fivt.students.surakshina.shell.Shell;
import ru.fizteh.fivt.students.surakshina.shell.ShellCommands;

public class Commands extends Shell {

    public static FileMap currentfileMap;
    public static File currentFile;

    @Override
    protected String[] extractArgumentsFromInputString(String input) {
        input = input.trim();
        if (input.isEmpty()) {
            return null;
        }
        return input.split("[\\s]+", 3);
    }

    @Override
    protected String rewriteInput(String current) {
        return current;
    }

    @Override
    public void executeProcess(String[] input) {
        if (input == null) {
            return;
        }
        switch (input[0]) {
        case "create":
            if (input.length == 2) {
                createTable(input[1]);
            } else {
                printError("Incorrect number of arguments");
            }
            break;
        case "drop":
            if (input.length == 2) {
                dropTable(input[1]);
            } else {
                printError("Incorrect number of arguments");
            }
            break;
        case "use":
            if (input.length == 2) {
                useTable(input[1]);
            } else {
                printError("Incorrect number of arguments");
            }
            break;
        case "exit": {
            saveTable();
            System.exit(0);
        }
            break;
        default:
            if (input[0].equals("put") || input[0].equals("get") || input[0].equals("remove")) {
                if (!FileMap.hasOpenedTable) {
                    System.out.println("no table");
                    return;
                } else {
                    byte c = 0;
                    c = (byte) Math.abs(input[1].getBytes(StandardCharsets.UTF_8)[0]);
                    int ndirectory = c % 16;
                    int nfile = c / 16 % 16;
                    FileMap.table[ndirectory][nfile].exec(input);
                }
            } else {
                printError("Incorrect input");
            }
        }
    }

    protected void saveTable() {
        if (FileMap.hasOpenedTable) {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    try {
                        currentFile = new File(FileMap.workingDirectory + File.separator + FileMap.currentTable
                                + File.separator + i + ".dir" + File.separator + j + ".dat");
                        FileMap.table[i][j].writeInDatabase(currentFile);
                        if (FileMap.table[i][j].fileMap.size() == 0) {
                            currentFile.delete();
                        }
                    } catch (FileNotFoundException e) {
                        printError("Can't read database");
                    } catch (IOException e1) {
                        printError("Can't write in database");
                    }
                }
                File directory = new File(FileMap.workingDirectory + File.separator + FileMap.currentTable
                        + File.separator + i + ".dir");
                if (directory.list().length == 0) {
                    deleteTable(directory);
                }
            }
            FileMap.hasOpenedTable = false;
        }

    }

    protected void useTable(String name) {
        File table = new File(FileMap.workingDirectory + File.separator + name);
        if (!table.exists()) {
            System.out.println(name + " not exists");
        } else {
            saveTable();
            FileMap.currentTable = name;
            outputTable();
            System.out.println("using " + name);
            FileMap.hasOpenedTable = true;
        }

    }

    private void check(HashMap<String, String> map, int directoryNumber, int fileNumber) {
        if (map.size() != 0) {
            for (String key : map.keySet()) {
                byte c = 0;
                c = (byte) Math.abs(key.getBytes(StandardCharsets.UTF_8)[0]);
                int ndirectory = c % 16;
                int nfile = c / 16 % 16;
                if (ndirectory != directoryNumber || nfile != fileNumber) {
                    printError("Illegal files");
                }
            }
        }
    }

    protected void outputTable() {
        if (!FileMap.hasOpenedTable) {
            for (int i = 0; i < 16; ++i) {
                File directory = new File(FileMap.workingDirectory + File.separator + FileMap.currentTable
                        + File.separator + i + ".dir");
                if (!directory.exists()) {
                    if (!directory.mkdir()) {
                        printError("Can't create a new directory");
                    }
                } else if (!directory.isDirectory()) {
                    printError("Incorrect files in the table");
                }
                for (int j = 0; j < 16; ++j) {
                    File file = new File(FileMap.workingDirectory + File.separator + FileMap.currentTable
                            + File.separator + i + ".dir" + File.separator + j + ".dat");
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            printError("Can't create new file");
                        }
                    }
                    if (FileMap.table[i][j] == null) {
                        FileMap.table[i][j] = new FileMap();
                    }
                    try {
                        FileMap.table[i][j].readDatabase(file);
                        currentfileMap = FileMap.table[i][j];
                        currentFile = file;
                    } catch (FileNotFoundException e) {
                        printError("Can't read database");
                    } catch (IOException e1) {
                        printError("Can't read from database");
                    }
                    if (FileMap.table[i][j].fileMap.size() != 0) {
                        check(FileMap.table[i][j].fileMap, i, j);
                    }
                }
            }
            FileMap.hasOpenedTable = true;
        }
    }

    protected void dropTable(String name) {
        File table = new File(FileMap.workingDirectory + File.separator + name);
        if (!table.exists()) {
            System.out.println(name + " not exists");
        } else {
            deleteTable(table);
            System.out.println("dropped");
        }

    }

    protected void deleteTable(File tmp) {
        if (tmp.exists()) {
            if (!tmp.isDirectory() || tmp.listFiles().length == 0) {
                if (!tmp.delete()) {
                    printError("Can't delete file");
                }
            } else {
                while (tmp.listFiles().length != 0) {
                    if (tmp.listFiles()[0].exists()) {
                        deleteTable(tmp.listFiles()[0]);
                    }
                }
                if (!tmp.delete()) {
                    printError("Can't delete file");
                }
            }
        }
    }

    private void createTable(String name) {
        File table = new File(FileMap.workingDirectory + File.separator + name);
        if (table.exists()) {
            System.out.println(name + " exists");
        } else {
            if (!table.mkdir()) {
                printError("Cannot create new directory");
            }
            System.out.println("created");
        }
    }

    @Override
    public void printError(String s) {
        if (isInteractive) {
            System.out.println(s);
        } else {
            System.err.println(s);
            try {
                FileMap fileMap = new FileMap();
                fileMap.writeInDatabase(currentFile);
            } catch (FileNotFoundException e) {
                System.err.println("Can't read database");
                FileMap.closeFile();
                System.exit(1);
            } catch (IOException e1) {
                System.err.println("Can't write in database");
                FileMap.closeFile();
                System.exit(1);
            }
            FileMap.closeFile();
            System.exit(1);
        }
    }

    protected void getFile(String key) {
        byte c = 0;
        c = (byte) Math.abs(key.getBytes(StandardCharsets.UTF_8)[0]);
        int ndirectory = c % 16;
        int nfile = c / 16 % 16;
        currentFile = new File(FileMap.workingDirectory + File.separator + FileMap.currentTable + File.separator
                + ndirectory + ".dir" + File.separator + nfile + ".dat");
        currentfileMap = FileMap.table[ndirectory][nfile];
    }

    protected void put(String key, String value) {
        getFile(key);
        if (currentfileMap.fileMap.containsKey(key)) {
            if (currentfileMap.fileMap.get(key) != null) {
                System.out.println("overwrite\n" + currentfileMap.fileMap.get(key));
                currentfileMap.fileMap.put(key, value);
            } else {
                System.out.println("new");
            }
        } else {
            currentfileMap.fileMap.put(key, value);
            System.out.println("new");
        }
    }

    protected void get(String key) {
        getFile(key);
        if (currentfileMap.fileMap.containsKey(key)) {
            System.out.println("found\n" + currentfileMap.fileMap.get(key));
        } else {
            System.out.println("not found");
        }
    }

    public void workWithFileMap(String[] input) {
        ShellCommands sh = new ShellCommands();
        sh.workWithShell(input);
    }

    protected void remove(String key) {
        getFile(key);
        if (currentfileMap.fileMap.containsKey(key)) {
            currentfileMap.fileMap.remove(key);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }

}
