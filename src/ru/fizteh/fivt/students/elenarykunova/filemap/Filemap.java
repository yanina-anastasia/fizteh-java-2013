package ru.fizteh.fivt.students.elenarykunova.filemap;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.elenarykunova.shell.Shell;

public class Filemap {

    DataBase[][] data = new DataBase[16][16];
    String currTable = null;
    static String rootDir = null;

    public void saveChanges() {
        if (currTable == null) {
            return;
        }
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (data[i][j].hasFile()) {
                    try {
                        data[i][j].commitChanges();
                    } catch (IOException e) {
                        System.err.println("can't write to file");
                        data[i][j].closeDataFile();
                        System.exit(1);
                    }
                }
            }
        }
        for (int i = 0; i < 16; i++) {
            File tmpDir = new File(currTable + File.separator + i + ".dir");
            if (tmpDir.exists() && tmpDir.list().length == 0) {
                Shell.rm(tmpDir.getAbsolutePath());
            }
        }
    }

    public void load(String table) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                data[i][j] = new DataBase(currTable, i, j, false);
            }
        }
    }

    public void changeTable(String newTable) {
        if (currTable != null) {
            saveChanges();
        }
        currTable = rootDir + File.separator + newTable;
        load(newTable);
    }

    public String getRootDir() {
        String rootDir = System.getProperty("fizteh.db.dir");
        if (rootDir == null) {
            System.err.println(rootDir + ": no directory");
            System.exit(1);
        }
        File tmpDir = new File(rootDir);
        if (!tmpDir.exists()) {
            System.err.println(rootDir + ": can't open directory");
            System.exit(1);
        } else if (!tmpDir.isDirectory()) {
            System.err.println(rootDir + ": isn't a directory");
            System.exit(1);
        }
        return rootDir;
    }

    public Filemap() {
        rootDir = getRootDir();
    }

    public static void main(String[] args) {
        Filemap mfm = new Filemap();
        ExecuteCmd cmd = new ExecuteCmd(rootDir, mfm);
        cmd.workWithUser(args);
        mfm.saveChanges();
    }
}
