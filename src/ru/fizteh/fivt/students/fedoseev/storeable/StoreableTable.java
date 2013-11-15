package ru.fizteh.fivt.students.fedoseev.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreableTable implements Table {
    private static final int MAX_TABLE_SIZE = 4 * 1024 * 1024;
    private static final int MAX_FILE_SIZE = 1024 * 1024;
    private static final int DIRS_NUMBER = 16;
    private static final int DIR_FILES_NUMBER = 16;

    private String tableName;
    private Map<String, Storeable> contents;
    private Map<String, Storeable> diff;
    private long tableSize;
    private boolean[] boolUsedDirs;
    private boolean[][] boolUsedFiles;
    private int number;
    private StoreableTableProvider tb;
    private ArrayList<Class<?>> columnTypes;

    public StoreableTable(String tableName, List<Class<?>> columnTypes, StoreableTableProvider prev) {
        this.tableName = tableName;
        tableSize = 0;
        tb = prev;

        boolUsedDirs = new boolean[DIRS_NUMBER];
        boolUsedFiles = new boolean[DIRS_NUMBER][DIR_FILES_NUMBER];
        contents = new HashMap<>();
        diff = new HashMap<>();

        number = contents.size();
        this.columnTypes = new ArrayList<>(columnTypes);
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public Storeable get(String key) {
        checkKeyFormat(key);

        if (diff.containsKey(key)) {
            return diff.get(key);
        }

        return contents.get(key);
    }

    @Override
    public Storeable put(String key, Storeable value) {
        checkKeyFormat(key);
        if (value == null) {
            throw new IllegalArgumentException("PUT ERROR: invalid value");
        }
        try {
            for (int i = 0; i < columnTypes.size(); i++) {
                Object v = value.getColumnAt(i);

                if (v != null && !columnTypes.get(i).equals(v.getClass())) {
                    throw new ColumnFormatException("PUT ERROR: invalid value");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("PUT ERROR: invalid value");
        }

        boolean throwed = false;

        try {
            value.getColumnAt(columnTypes.size());
        } catch (IndexOutOfBoundsException e) {
            throwed = true;
        } finally {
            if (!throwed) {
                throw new ColumnFormatException("PUT ERROR: invalid value");
            }
        }

        if (!diff.containsKey(key) && !contents.containsKey(key) || diff.containsKey(key) && diff.get(key) == null) {
            number++;
        }

        getBoolUsedDirs()[dirHash(key)] = true;
        getBoolUsedFiles()[dirHash(key)][fileHash(key)] = true;

        Storeable prevValue = get(key);

        diff.put(key, value);

        if (value.equals(contents.get(key))) {
            diff.remove(key);
        }

        return prevValue;
    }

    @Override
    public Storeable remove(String key) {
        checkKeyFormat(key);

        if (!diff.containsKey(key) && contents.get(key) != null || diff.get(key) != null) {
            number--;
        }

        getBoolUsedDirs()[dirHash(key)] = true;
        getBoolUsedFiles()[dirHash(key)][fileHash(key)] = true;

        Storeable prevValue = get(key);

        diff.put(key, null);

        if (contents.get(key) == null) {
            diff.remove(key);
        }

        return prevValue;
    }

    @Override
    public int size() {
        return number;
    }

    @Override
    public int commit() {
        for (String key : diff.keySet()) {
            if (diff.get(key) == null) {
                contents.remove(key);
            } else {
                contents.put(key, diff.get(key));
            }
        }

        int prevSize = diff.size();

        diff.clear();

        return prevSize;
    }

    @Override
    public int rollback() {
        int prevSize = diff.size();

        diff.clear();
        number = contents.size();

        return prevSize;
    }

    public void checkTable() throws IOException {
        if (this.getTableSize() > MAX_TABLE_SIZE) {
            AbstractStoreable.saveTable(this);
            clearContentAndDiff();
        }
    }

    public void checkFile(RandomAccessFile raf) throws IOException {
        if (raf.length() == 0) {
            raf.close();

            throw new IOException("ERROR: empty file");
        }
        if (raf.length() > MAX_FILE_SIZE) {
            raf.close();

            throw new IOException("ERROR: too big file");
        }
    }

    public int getDirsNumber() {
        return DIRS_NUMBER;
    }

    public int getDirFilesNumber() {
        return DIR_FILES_NUMBER;
    }

    public void setTableSize(long tableSize) {
        this.tableSize = tableSize;
    }

    public long getTableSize() {
        return tableSize;
    }

    public int getDiffSize() {
        return diff.size();
    }

    public Map<String, Storeable> getMapContents() {
        return contents;
    }

    public File getCurTableDir() {
        return new File(tableName);
    }

    public void clearContentAndDiff() {
        contents.clear();
        diff.clear();
    }

    public int dirHash(String key) {
        return Math.abs(key.substring(0).getBytes(StandardCharsets.UTF_8)[0]) % DIRS_NUMBER;
    }

    public int fileHash(String key) {
        return Math.abs(key.substring(0).getBytes(StandardCharsets.UTF_8)[0]) / DIRS_NUMBER % DIR_FILES_NUMBER;
    }

    public void putMapTable(Map<String, Storeable> map) {
        if (map != null) {
            for (String key : map.keySet()) {
                put(key, map.get(key));
            }
        }
    }

    public boolean[] getBoolUsedDirs() {
        return boolUsedDirs;
    }

    public boolean[][] getBoolUsedFiles() {
        return boolUsedFiles;
    }

    public void setUsedDirs() {
        for (String key : getMapContents().keySet()) {
            boolUsedDirs[dirHash(key)] = true;
        }
    }

    public void clearUsedDirs() {
        for (int i = 0; i < DIRS_NUMBER; i++) {
            boolUsedDirs[i] = false;
        }
    }

    public void clearUsedFiles() {
        for (int i = 0; i < DIR_FILES_NUMBER; i++) {
            for (int j = 0; j < DIR_FILES_NUMBER; j++) {
                boolUsedFiles[i][j] = false;
            }
        }
    }

    public StoreableTableProvider getTb() {
        return tb;
    }

    private void checkKeyFormat(String key) {
        if (key == null || !key.matches("[\\S]+")) {
            throw new IllegalArgumentException("GET | PUT | REMOVE ERROR: invalid key");
        }
    }

    @Override
    public int getColumnsCount() {
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= getColumnsCount()) {
            throw new IndexOutOfBoundsException("ERROR: invalid column index");
        }

        return columnTypes.get(columnIndex);
    }

    public void checkKeyPlacement(String key, File dir, File file) throws IOException {
        String fileName = file.getName();
        int fileNumber = Integer.parseInt(fileName.substring(0, fileName.indexOf('.')));
        String dirName = dir.getName();
        int dirNumber = Integer.parseInt(dirName.substring(0, dirName.indexOf('.')));

        if (fileHash(key) != fileNumber || dirHash(key) != dirNumber) {
            throw new IOException("ERROR: wrong key placement");
        }
    }
}
