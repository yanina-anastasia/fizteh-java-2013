package ru.fizteh.fivt.students.drozdowsky.database;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class FileHashMap {
    private static final int NDIRS = 16;
    private static final int NFILES = 16;

    private File db;
    private FileMap[][] base;

    FileHashMap(File db) throws IOException {
        this.db = db;
        base = new FileMap[NDIRS][NFILES];
        readDB();
    }

    public boolean put(String[] args) {
        if (args.length != 3) {
            error("usage: put key value");
            return false;
        }

        int nDir = getDirNum(args[1]);
        int nFile = getFileNum(args[1]);

        if (base[nDir][nFile] == null) {
            File dirPath = new File(db.getAbsolutePath() + '/' + Integer.toString(nDir));
            if (!dirPath.exists()) {
                dirPath.mkdir();
            }
            File filePath = new File(dirPath.getAbsolutePath() + '/' + Integer.toString(nFile));
            try {
                filePath.createNewFile();
                base[nDir][nFile] = new FileMap(filePath);
                base[nDir][nFile].put(args);
            } catch (IOException e) {
                error(e.getMessage());
                return false;
            }
            return true;
        }
        return base[nDir][nFile].put(args);
    }

    public boolean get(String[] args) {
        if (args.length != 2) {
            error("usage: get key");
            return false;
        }

        int nDir = getDirNum(args[1]);
        int nFile = getFileNum(args[1]);

        if (base[nDir][nFile] == null) {
            System.out.println("not found");
            return true;
        }
        return base[nDir][nFile].get(args);
    }

    public boolean remove(String[] args) {
        if (args.length != 2) {
            error("usage: remove key");
            return false;
        }

        int nDir = getDirNum(args[1]);
        int nFile = getFileNum(args[1]);

        if (base[nDir][nFile] == null) {
            System.out.println("not found");
            return true;
        }
        return base[nDir][nFile].remove(args);
    }

    public boolean exit(String[] args) {
        if (args.length != 1) {
            error("usage: exit");
            return false;
        }
        close();
        System.exit(0);
        return true;
    }

    public String getPath() {
        return db.getAbsolutePath();
    }

    public void close() {
        writeDB();
    }

    private int getDirNum(String key) {
        byte b = key.getBytes()[0];
        if (b < 0) {
            b *= -1;
        }
        int nDir = b % 16;
        int nFile = (b / 16) % 16;
        return nDir;
    }

    private int getFileNum(String key) {
        byte b = key.getBytes()[0];
        if (b < 0) {
            b *= -1;
        }
        int nDir = b % 16;
        int nFile = (b / 16) % 16;
        return nFile;
    }

    private void readDB() throws IOException {
        if (!db.getParentFile().exists()) {
            fatalError(db.getParentFile().getAbsolutePath() + ": No such file or directory");
        } else if (db.exists() && !db.isDirectory()) {
            fatalError(db.getAbsolutePath() + ": Not a directory");
        } else if (!db.exists()) {
            if (!db.mkdir()) {
                fatalError(db.getAbsolutePath() + ": Cannot create database");
            }
            return;
        }

        String[] directories = db.list();
        for (String directory : directories) {
            int nDir = nameInRange(directory, NDIRS);
            if (nDir == -1 || !(new File(db.getAbsolutePath() + '/' + directory).isDirectory())) {
                fatalError(db.getAbsolutePath() + ": Not valid database1");
            }

            File subdir = new File(db.getAbsolutePath() + '/' + directory);
            String[] files = subdir.list();
            for (String file : files) {
                int nFile = nameInRange(file, NFILES);
                if (nFile == -1 || !(new File(subdir.getAbsolutePath() + '/' + file).isFile())) {
                    fatalError(db.getAbsolutePath() + ": Not valid database2");
                }

                base[nDir][nFile] = new FileMap(new File(subdir.getAbsolutePath() + '/' + file));
                Set<String> keys = base[nDir][nFile].getKeys();
                if (keys.size() == 0) {
                    fatalError(db.getAbsolutePath() + ": Not valid database3");
                }
                for (String key : keys) {
                    int realNDir = getDirNum(key);
                    int realNFile = getFileNum(key);
                    if (!(nDir == realNDir && nFile == realNFile)) {
                        fatalError(db.getAbsolutePath() + nDir + " " + nFile + ": Not valid database1");
                    }
                }
            }
        }
    }

    private void writeDB() {
        for (int i = 0; i < NDIRS; i++) {
            File dirPath = new File(db.getAbsolutePath() + '/' + Integer.toString(i));
            for (int j = 0; j < NFILES; j++) {
                if (base[i][j] != null) {
                    File filePath = new File(dirPath.getAbsolutePath() + '/' + Integer.toString(j));
                    if (base[i][j].getKeys().size() == 0) {
                        filePath.delete();
                    } else {
                        base[i][j].close();
                    }
                }
            }
            if (dirPath.exists()) {
                dirPath.delete();
            }
        }
    }

    private int nameInRange(String s, int range) {
        for (int i = 0; i < range; i++) {
            if (Integer.toString(i).equals(s)) {
                return i;
            }
        }
        return -1;
    }

    private void fatalError(String error) throws IOException {
        throw new IOException(error);
    }

    private void error(String aError) {
        System.err.println(aError);
    }
}