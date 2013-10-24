package ru.fizteh.fivt.students.vorotilov.multidb;

import ru.fizteh.fivt.students.vorotilov.db.DataBaseFile;
import ru.fizteh.fivt.students.vorotilov.db.DataBaseOpenFailed;
import ru.fizteh.fivt.students.vorotilov.db.HashcodeDestination;


import java.io.File;
import java.io.IOException;

public class DataBase {
    File dbDirectory;
    protected DataBaseFile[][] dbFiles;
    protected boolean[][] wasFileModified;

    DataBase(File dbDirectory) throws IOException, DbDirectoryException {
        this.dbDirectory = dbDirectory;
        dbFiles = new DataBaseFile[16][16];
        wasFileModified = new boolean[16][16];
        if (!dbDirectory.exists()) {
            throw new DbDirectoryException("proposed directory not exists");
        }
        if (!dbDirectory.isDirectory()) {
            throw new DbDirectoryException("proposed object is not a directory");
        }
        File[] subDirs = dbDirectory.listFiles();
        if (subDirs != null) {
            for (File i: subDirs) {
                int numberOfSubdir;
                try {
                    if (!i.isDirectory()) {
                        throw new DbDirectoryException("sub object is not directory");
                    }
                    String[] dbSubDirName = i.getName().split("[.]");
                    numberOfSubdir = Integer.parseInt(dbSubDirName[0]);
                    if (numberOfSubdir < 0 || numberOfSubdir > 15
                            || !dbSubDirName[1].equals("dir") || dbSubDirName.length != 2) {
                        throw new DbDirectoryException("root directory contains not 0.dir ... 15.dir");
                    }
                } catch (NumberFormatException e) {
                    throw new DbDirectoryException("db root directory contains not 0..15 directory");
                }
                File[] listSubFiles = i.listFiles();
                if (listSubFiles != null) {
                    for (File j: listSubFiles) {
                        int numberOfDbFile;
                        try {
                            if (!j.isFile()) {
                                throw new DbDirectoryException("db object is not file");
                            }
                            String[] dbFileName = j.getName().split("[.]");
                            numberOfDbFile = Integer.parseInt(dbFileName[0]);
                            if (numberOfDbFile < 0 || numberOfDbFile > 15
                                    || !dbFileName[1].equals("dat") || dbFileName.length != 2) {
                                throw new DbDirectoryException("db sub directory contains not 0.dat ... 15.dat");
                            } else {
                                try {
                                    dbFiles[numberOfSubdir][numberOfDbFile] = new DataBaseFile(j);
                                } catch (DataBaseOpenFailed e) {
                                    System.out.print("can't open db file");
                                    System.exit(1);
                                }
                            }
                        }  catch (NumberFormatException e) {
                            throw new DbDirectoryException("db sub directory contains not 0.dat ... 15.dat");
                        }
                    }
                }
            }
        }
    }

    private DataBaseFile getRequiredFile(int ndirectory, int nfile)
            throws IOException, DataBaseOpenFailed, DbDirectoryException {
        if (dbFiles[ndirectory][nfile] != null) {
            return dbFiles[ndirectory][nfile];
        } else {
            File subDir = new File(dbDirectory, Integer.toString(ndirectory) + ".dir");
            if (!subDir.exists()) {
                if (!subDir.mkdir()) {
                    throw new DbDirectoryException("can't create sub dir");
                }
            }
            File subFile = new File(subDir, Integer.toString(nfile) + ".dat");
            dbFiles[ndirectory][nfile] = new DataBaseFile(subFile);
        }
        return dbFiles[ndirectory][nfile];
    }

    public void put(String newKey, String newValue) throws IOException, DataBaseOpenFailed, DbDirectoryException {
        HashcodeDestination dest = new HashcodeDestination(newKey);
        wasFileModified[dest.ndirectory][dest.nfile] = true;
        getRequiredFile(dest.ndirectory, dest.nfile).put(newKey, newValue);
    }

    public String get(String newKey) throws IOException, DataBaseOpenFailed, DbDirectoryException {
        HashcodeDestination dest = new HashcodeDestination(newKey);
        return getRequiredFile(dest.ndirectory, dest.nfile).get(newKey);
    }

    public String remove(String newKey) throws IOException, DataBaseOpenFailed, DbDirectoryException {
        HashcodeDestination dest = new HashcodeDestination(newKey);
        String removedValue = getRequiredFile(dest.ndirectory, dest.nfile).remove(newKey);
        if (removedValue != null) {
            wasFileModified[dest.ndirectory][dest.nfile] = true;
        }
        return removedValue;
    }

    public void save() {
        try {
            for (int i = 0; i < 16; ++i) {
                File subDir = new File(dbDirectory, Integer.toString(i));
                for (int j = 0; j < 16; ++j) {
                    if (wasFileModified[i][j]) {
                        if (dbFiles[i][j].isEmpty()) {
                            dbFiles[i][j].close();
                            File currentDbFile = new File(subDir, Integer.toString(j) + ".dat");
                            if (!currentDbFile.delete()) {
                                System.out.println("can't delete empty db file:'"
                                        + currentDbFile.getCanonicalPath() + "'");
                                System.exit(1);
                            }
                        } else {
                            dbFiles[i][j].save();
                        }
                    }
                }
                if (subDir.exists() && subDir.listFiles() == null) {
                    if (!subDir.delete()) {
                        System.out.println("can't delete empty sub dir:'" + subDir.getCanonicalPath() + "'");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("can't save db file");
            System.exit(1);
        }

    }

    public void close() {
        for (DataBaseFile[] i : dbFiles) {
            for (DataBaseFile j : i) {
                if (j != null) {
                    j.close();
                }
            }
        }
    }
}
