package ru.fizteh.fivt.students.eltyshev.multifilemap;

import ru.fizteh.fivt.students.eltyshev.filemap.base.AbstractTable;
import ru.fizteh.fivt.students.eltyshev.filemap.base.FileMapUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MultifileTable extends AbstractTable {
    private static final int BACKET_COUNT = 16;
    private static final int FILES_PER_DIR = 16;

    public MultifileTable(String directory, String tableName) {
        super(directory, tableName);
    }

    protected void save() throws IOException {
        File tableDirectory = getTableDirectory();
        ArrayList<Set<String>> keysToSave = new ArrayList<Set<String>>();
        boolean isBacketEmpty;

        for (int backetNumber = 0; backetNumber < BACKET_COUNT; ++backetNumber) {
            keysToSave.clear();
            for (int index = 0; index < FILES_PER_DIR; ++index) {
                keysToSave.add(new HashSet<String>());
            }
            isBacketEmpty = true;

            for (final String key : oldData.keySet()) {
                if (getDirNumber(key) == backetNumber) {
                    int fileNumber = getFileNumber(key);
                    keysToSave.get(fileNumber).add(key);
                    isBacketEmpty = false;
                }
            }

            String backetName = String.format("%d.dir", backetNumber);
            File backetDirectory = new File(tableDirectory, backetName);

            if (isBacketEmpty) {
                MultifileMapUtils.deleteFile(backetDirectory);
            }

            for (int fileNumber = 0; fileNumber < FILES_PER_DIR; ++fileNumber) {
                String fileName = String.format("%d.dat", fileNumber);
                File file = new File(backetDirectory, fileName);
                if (keysToSave.get(fileNumber).isEmpty()) {
                    MultifileMapUtils.deleteFile(file);
                    continue;
                }
                if (!backetDirectory.exists()) {
                    backetDirectory.mkdir();
                }
                saveToFile(keysToSave.get(fileNumber), file.getAbsolutePath());
            }
        }
    }

    protected void load() throws IOException {
        File tableDirectory = getTableDirectory();
        for (final File backet : tableDirectory.listFiles()) {
            for (final File file : backet.listFiles()) {
                loadFromFile(file.getAbsolutePath());
            }
        }
    }

    private File getTableDirectory() {
        File tableDirectory = new File(getDirectory(), getName());
        if (!tableDirectory.exists()) {
            tableDirectory.mkdir();
        }
        return tableDirectory;
    }

    private int getDirNumber(String key) {
        byte[] bytes = key.getBytes(CHARSET);
        return bytes[0] % BACKET_COUNT;
    }

    private int getFileNumber(String key) {
        byte[] bytes = key.getBytes(CHARSET);
        return bytes[0] / BACKET_COUNT % FILES_PER_DIR;
    }
}