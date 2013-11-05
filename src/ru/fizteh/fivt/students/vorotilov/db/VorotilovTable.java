package ru.fizteh.fivt.students.vorotilov.db;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.util.*;

public class VorotilovTable implements Table {

    private final File tableRootDir;
    private TableFile[][] tableFiles;
    private boolean[][] tableFileModified;
    private HashMap<String, String> tableIndexedData;
    private int numberOfUncommittedChanges;

    private void index() {
        tableFiles = new TableFile[16][16];
        tableFileModified = new boolean[16][16];
        tableIndexedData = new HashMap<>();
        File[] subDirsList = tableRootDir.listFiles();
        if (subDirsList != null) {
            for (File subDir: subDirsList) {
                int numberOfSubDir;
                if (!subDir.isDirectory()) {
                    throw new IllegalStateException("In table root dir found object is not a directory");
                }
                String[] tableSubDirName = subDir.getName().split("[.]");
                try {
                    numberOfSubDir = Integer.parseInt(tableSubDirName[0]);
                } catch (NumberFormatException e) {
                    throw new IllegalStateException("Table root directory contains not 0.dir ... 15.dir");
                }
                if (numberOfSubDir < 0 || numberOfSubDir > 15
                        || !tableSubDirName[1].equals("dir") || tableSubDirName.length != 2) {
                    throw new IllegalStateException("Table root directory contains not 0.dir ... 15.dir");
                }
                File[] subFilesList = subDir.listFiles();
                if (subFilesList != null) {
                    for (File subFile: subFilesList) {
                        int numberOfSubFile;
                        if (!subFile.isFile()) {
                            throw new IllegalStateException("In table sub dir found object is not a file");
                        }
                        String[] dbFileName = subFile.getName().split("[.]");
                        try {
                            numberOfSubFile = Integer.parseInt(dbFileName[0]);
                        } catch (NumberFormatException e) {
                            throw new IllegalStateException("Table sub directory contains not 0.dat ... 15.dat");
                        }
                        if (numberOfSubFile < 0 || numberOfSubFile > 15
                                || !dbFileName[1].equals("dat") || dbFileName.length != 2) {
                            throw new IllegalStateException("Table sub directory contains not 0.dat ... 15.dat");
                        } else {
                            tableFiles[numberOfSubDir][numberOfSubFile] = new TableFile(subFile);
                            tableFiles[numberOfSubDir][numberOfSubFile].setReadMode();
                            while (tableFiles[numberOfSubDir][numberOfSubFile].hasNext()) {
                                TableFile.Entry tempEntry = tableFiles[numberOfSubDir][numberOfSubFile].readEntry();
                                tableIndexedData.put(tempEntry.getKey(), tempEntry.getValue());
                            }
                        }
                    }
                }
            }
        }
        numberOfUncommittedChanges = 0;
    }

    VorotilovTable(File tableRootDir) {
        if (tableRootDir == null) {
            throw new IllegalArgumentException("Table root dir is null");
        } else if (!tableRootDir.exists()) {
            throw new IllegalArgumentException("Proposed root dir not exists");
        } else if (!tableRootDir.isDirectory()) {
            throw new IllegalArgumentException("Proposed object is not directory");
        }
        this.tableRootDir = tableRootDir;
        index();
    }

    @Override
    public String getName() {
        return tableRootDir.getName();
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        return tableIndexedData.get(key);
    }

    @Override
    public String put(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        String oldValue = tableIndexedData.get(key);
        if (oldValue == null || !oldValue.equals(value)) {
            HashcodeDestination dest = new HashcodeDestination(key);
            tableFileModified[dest.getDir()][dest.getFile()] = true;
            tableIndexedData.put(key, value);
            ++numberOfUncommittedChanges;
        }
        return oldValue;
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        String oldValue = tableIndexedData.remove(key);
        if (oldValue != null) {
            HashcodeDestination dest = new HashcodeDestination(key);
            tableFileModified[dest.getDir()][dest.getFile()] = true;
            ++numberOfUncommittedChanges;
        }
        return oldValue;
    }

    @Override
    public int size() {
        return tableIndexedData.size();
    }

    @Override
    public int commit() {
        int numberOfCommittedChanges = numberOfUncommittedChanges;
        Set<Map.Entry<String, String>> dbSet = tableIndexedData.entrySet();
        Iterator<Map.Entry<String, String>> i = dbSet.iterator();
        for (int nDir = 0; nDir < 16; ++nDir) {
            for (int nFile = 0; nFile < 16; ++nFile) {
                if (tableFileModified[nDir][nFile]) {
                    if (tableFiles[nDir][nFile] == null) {
                        File subDir = new File(tableRootDir, Integer.toString(nDir) + ".dir");
                        File subFile = new File(subDir, Integer.toString(nFile) + ".dat");
                        if (!subDir.exists()) {
                            if (!subDir.mkdir()) {
                                throw new IllegalStateException("Sub dir was not created");
                            }
                        }
                        tableFiles[nDir][nFile] = new TableFile(subFile);
                    }
                    tableFiles[nDir][nFile].setWriteMode();
                }
            }
        }
        while (i.hasNext()) {
            Map.Entry<String, String> tempMapEntry = i.next();
            HashcodeDestination dest = new HashcodeDestination(tempMapEntry.getKey());
            if (tableFileModified[dest.getDir()][dest.getFile()]) {
                tableFiles[dest.getDir()][dest.getFile()].writeEntry(tempMapEntry.getKey(), tempMapEntry.getValue());
            }
        }
        for (int nDir = 0; nDir < 16; ++nDir) {
            for (int nFile = 0; nFile < 16; ++nFile) {
                tableFileModified[nDir][nFile] = false;
            }
        }
        return numberOfCommittedChanges;
    }

    @Override
    public int rollback() {
        int numberOfRolledChanges = numberOfUncommittedChanges;
        numberOfUncommittedChanges = 0;
        for (int nDir = 0; nDir < 16; ++nDir) {
            for (int nFile = 0; nFile < 16; ++nFile) {
                if (tableFileModified[nDir][nFile]) {
                    tableFileModified[nDir][nFile] = false;
                    tableFiles[nDir][nFile].setReadMode();
                    while (tableFiles[nDir][nFile].hasNext()) {
                        TableFile.Entry tempEntry = tableFiles[nDir][nFile].readEntry();
                        tableIndexedData.put(tempEntry.getKey(), tempEntry.getValue());
                    }
                }
            }
        }
        return numberOfRolledChanges;
    }

    public int uncommittedChanges() {
        return numberOfUncommittedChanges;
    }

    public void close() throws Exception {
        for (int nDir = 0; nDir < 16; ++nDir) {
            for (int nFile = 0; nFile < 16; ++nFile) {
                if (tableFiles[nDir][nFile] != null) {
                    tableFiles[nDir][nFile].close();
                }
            }
        }
        File[] listOfSubDirs = tableRootDir.listFiles();
        if (listOfSubDirs != null) {
            for (File subDir : listOfSubDirs) {
                if (subDir.exists()) {
                    File[] listOfFiles = subDir.listFiles();
                    if (listOfFiles != null && listOfFiles.length == 0) {
                        if (!subDir.delete()) {
                            throw new IllegalStateException("Can't delete empty sub dir");
                        }
                    }
                }
            }
        }
    }
}
