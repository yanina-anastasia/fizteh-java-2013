package ru.fizteh.fivt.students.inaumov.multifilemap;

import ru.fizteh.fivt.students.inaumov.filemap.AbstractTable;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MultiFileTable extends AbstractTable {
    private static final int TABLES_NUM = 16;
    private static final int TABLES_IN_ONE_DIR = 16;

    public MultiFileTable(String dir, String tableName) throws IOException, IllegalArgumentException {
        super(dir, tableName);
    }

    public void saveTable() {
        File tableDir = getTableDir();

        ArrayList< Set<String> > keysToSave = new ArrayList< Set<String> >();
        boolean bucketIsEmpty;

        for (int bucketNumber = 0; bucketNumber < TABLES_NUM; ++bucketNumber) {
            keysToSave.clear();
            for (int i = 0; i < TABLES_IN_ONE_DIR; ++i) {
                keysToSave.add(new HashSet<String>());
            }
            bucketIsEmpty = true;

            for (final String key: tableHash.keySet()) {
                if (getDirNumber(key) == bucketNumber) {
                    int fileNumber = getFileNumber(key);
                    keysToSave.get(fileNumber).add(key);
                    bucketIsEmpty = false;
                }
            }

            String bucketName = bucketNumber + ".dir";
            File bucketDir = new File(getDir(), bucketName);

            if (bucketIsEmpty) {
                MultiFileMapUtils.deleteFile(bucketDir);
            }

            for (int fileNumber = 0; fileNumber < TABLES_IN_ONE_DIR; ++fileNumber) {
                String fileName = fileNumber + ".dat";
                File file = new File(bucketDir, fileName);

                if (keysToSave.get(fileNumber).isEmpty()) {
                    MultiFileMapUtils.deleteFile(file);
                    continue;
                }

                if (!bucketDir.exists()) {
                    bucketDir.mkdir();
                }
                // FileMapWriter
            }
        }
    }

    public void loadTable() {
        File tableDir = getTableDir();
        for (final File bucket: tableDir.listFiles()) {
            for (final File file: bucket.listFiles()) {
                // FileMapReader
            }
        }
    }

    private File getTableDir() {
        File tableDir = new File(getDir(), getName());
        if ( !tableDir.exists() ) {
            tableDir.mkdir();
        }

        return tableDir;
    }

    private int getDirNumber(String key) {
        byte[] bytes = key.getBytes(CHARSET);
        int firstSymbol = bytes[0] + 128;

        return firstSymbol % TABLES_NUM;
    }

    private int getFileNumber(String key) {
        byte[] bytes = key.getBytes(CHARSET);
        int firstSymbol = bytes[0] + 128;

        return firstSymbol / TABLES_NUM % TABLES_IN_ONE_DIR;
    }
}
