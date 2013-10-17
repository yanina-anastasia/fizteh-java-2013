package ru.fizteh.fivt.students.eltyshev.multifilemap;

import ru.fizteh.fivt.students.eltyshev.filemap.base.AbstractTable;
import ru.fizteh.fivt.students.eltyshev.filemap.base.FilemapReader;
import ru.fizteh.fivt.students.eltyshev.filemap.base.FilemapWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MultifileTable extends AbstractTable {
    private static final int BUCKET_COUNT = 16;
    private static final int FILES_PER_DIR = 16;

    public MultifileTable(String directory, String tableName) {
        super(directory, tableName);
    }

    protected void save() throws IOException {
        File tableDirectory = getTableDirectory();
        ArrayList<Set<String>> keysToSave = new ArrayList<Set<String>>();
        boolean isBucketEmpty;

        for (int bucketNumber = 0; bucketNumber < BUCKET_COUNT; ++bucketNumber) {
            keysToSave.clear();
            for (int index = 0; index < FILES_PER_DIR; ++index) {
                keysToSave.add(new HashSet<String>());
            }
            isBucketEmpty = true;

            for (final String key : oldData.keySet()) {
                if (getDirNumber(key) == bucketNumber) {
                    int fileNumber = getFileNumber(key);
                    keysToSave.get(fileNumber).add(key);
                    isBucketEmpty = false;
                }
            }

            String bucketName = String.format("%d.dir", bucketNumber);
            File bucketDirectory = new File(tableDirectory, bucketName);

            if (isBucketEmpty) {
                MultifileMapUtils.deleteFile(bucketDirectory);
            }

            for (int fileNumber = 0; fileNumber < FILES_PER_DIR; ++fileNumber) {
                String fileName = String.format("%d.dat", fileNumber);
                File file = new File(bucketDirectory, fileName);
                if (keysToSave.get(fileNumber).isEmpty()) {
                    MultifileMapUtils.deleteFile(file);
                    continue;
                }
                if (!bucketDirectory.exists()) {
                    bucketDirectory.mkdir();
                }
                FilemapWriter.saveToFile(file.getAbsolutePath(), keysToSave.get(fileNumber), oldData);
            }
        }
    }

    protected void load() throws IOException {
        File tableDirectory = getTableDirectory();
        for (final File bucket : tableDirectory.listFiles()) {
            for (final File file : bucket.listFiles()) {
                FilemapReader.loadFromFile(file.getAbsolutePath(), oldData);
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
        return bytes[0] % BUCKET_COUNT;
    }

    private int getFileNumber(String key) {
        byte[] bytes = key.getBytes(CHARSET);
        return bytes[0] / BUCKET_COUNT % FILES_PER_DIR;
    }
}