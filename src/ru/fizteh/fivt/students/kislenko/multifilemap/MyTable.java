package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MyTable implements Table {
    private String name;
    private Map<String, String> storage;
    private Map<String, String> newStorage;
    private boolean[][] uses;
    private long byteSize;
    private int count;
    private int newCount;

    public MyTable(String tableName) {
        name = tableName;
        storage = new HashMap<String, String>();
        byteSize = 0;
        uses = new boolean[16][16];
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j)
                uses[i][j] = false;
        }
        try {
            count = Utils.getTableSize(this);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            count = -1;
        }
        newCount = count;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Incorrect key to get.");
        }
        return storage.get(key);
    }

    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Incorrect key/value to put.");
        }
        if (storage.get(key) == null) {
            ++newCount;
        }
        return storage.put(key, value);
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Incorrect key to remove.");
        }
        if (storage.get(key) != null) {
            --newCount;
        }
        return storage.remove(key);
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public int commit() {
        throw new UnsupportedOperationException("WTF???");
    }

    @Override
    public int rollback() {
        throw new UnsupportedOperationException("WTF???");
    }

    public boolean isUsing(int nDirectory, int nFile) {
        return uses[nDirectory][nFile];
    }

    public void setUsing(int nDirectory, int nFile, boolean b) {
        uses[nDirectory][nFile] = b;
    }

    public void clear() {
        storage.clear();
    }

    public Map<String, String> getMap() {
        return storage;
    }

    public void setMap(Map<String, String> newMap) {
        storage = newMap;
    }

    public Path getPath() {
        File f = new File(name);
        return f.toPath();
    }

    public void setByteSize(long newSize) {
        byteSize = newSize;
    }

    public long getByteSize() {
        return byteSize;
    }

    public void setSize(int newSize) {
        count = newCount;
    }

    public void updateSize() {
        count = newCount;
    }

    public int getSize() {
        return newCount;
    }
}