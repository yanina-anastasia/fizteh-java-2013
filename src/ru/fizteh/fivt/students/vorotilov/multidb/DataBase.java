package ru.fizteh.fivt.students.vorotilov.multidb;

import ru.fizteh.fivt.students.vorotilov.db.DataBaseFile;
import ru.fizteh.fivt.students.vorotilov.db.DataBaseOpenFailed;


import java.io.File;
import java.io.IOException;

public class DataBase {
    File dbDirectory;
    protected DataBaseFile[][] db;
    protected boolean[][] wasFileModified;

    DataBase(File dbDirectory) throws IOException, DbDirectoryException {
        this.dbDirectory = dbDirectory;
        db = new DataBaseFile[16][16];
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
                    numberOfSubdir = Integer.parseInt(i.getName());
                    if (numberOfSubdir < 0 || numberOfSubdir > 15) {
                        throw new DbDirectoryException("db-root directory contains not 0..15 directory");
                    } else if (!i.isDirectory()) {
                        throw new DbDirectoryException("sub object is not directory");
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
                                    db[numberOfSubdir][numberOfDbFile] = new DataBaseFile(j);
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
        if (db[ndirectory][nfile] != null) {
            return db[ndirectory][nfile];
        } else {
            File subDir = new File(dbDirectory, Integer.toString(ndirectory));
            if (!subDir.exists()) {
                if (!subDir.mkdir()) {
                    throw new DbDirectoryException("can't create sub dir");
                }
            }
            File subFile = new File(subDir, Integer.toString(nfile) + ".dat");
            db[ndirectory][nfile] = new DataBaseFile(subFile);
        }
        return db[ndirectory][nfile];
    }

    public void put(String newKey, String newValue) throws IOException, DataBaseOpenFailed, DbDirectoryException {
        int hashcode = Math.abs(newKey.hashCode());
        int ndirectory = hashcode % 16;
        int nfile = hashcode / 16 % 16;
        wasFileModified[ndirectory][nfile] = true;
        getRequiredFile(ndirectory, nfile).put(newKey, newValue);
    }

    public String get(String newKey) throws IOException, DataBaseOpenFailed, DbDirectoryException {
        int hashcode = Math.abs(newKey.hashCode());
        int ndirectory = hashcode % 16;
        int nfile = hashcode / 16 % 16;
        return getRequiredFile(ndirectory, nfile).get(newKey);
    }

    public String remove(String newKey) throws IOException, DataBaseOpenFailed, DbDirectoryException {
        int hashcode = Math.abs(newKey.hashCode());
        int ndirectory = hashcode % 16;
        int nfile = hashcode / 16 % 16;
        String removedValue = getRequiredFile(ndirectory, nfile).remove(newKey);
        if (removedValue != null) {
            wasFileModified[ndirectory][nfile] = true;
        }
        return removedValue;
    }

    public void save() {
        try {
            for (int i = 0; i < 16; ++i) {
                File subDir = new File(dbDirectory, Integer.toString(i));
                for (int j = 0; j < 16; ++j) {
                    if (wasFileModified[i][j]) {
                        if (db[i][j].isEmpty()) {
                            db[i][j].close();
                            File currentDbFile = new File(subDir, Integer.toString(j) + ".dat");
                            if (!currentDbFile.delete()) {
                                System.out.println("can't delete empty db file:'"
                                        + currentDbFile.getCanonicalPath() + "'");
                                System.exit(1);
                            }
                        } else {
                            db[i][j].save();
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
        for (DataBaseFile[] i : db) {
            for (DataBaseFile j: i) {
                if (j != null) {
                    j.close();
                }
            }
        }
    }
}
