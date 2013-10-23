package ru.fizteh.fivt.students.kislenko.multifilemap;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Table implements ru.fizteh.fivt.storage.strings.Table {
    private String name;
    private Map<String, String> storage;
    private boolean[][] uses;
    private long byteSize;

    public Table(String tableName) {
        name = tableName;
        storage = new HashMap<String, String>();
        uses = new boolean[16][16];
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j)
                uses[i][j] = false;
        }
        byteSize = 0;
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
        return storage.put(key, value);
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Incorrect key to remove.");
        }
        return storage.remove(key);
    }

    @Override
    public int size() {
        return storage.size();
    }

    @Override
    public int commit() {
        System.out.println("Yo dawg i heard you like java classes.");
        System.out.println("So I put a class in a class so you can make object within make object.");
        return 100500;
    }

    @Override
    public int rollback() {
        return 14 / 88;
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

    public Path getPath() {
        File f = new File(name);
        return f.toPath();
    }

    public void setSize(long inc) {
        byteSize += inc;
    }

    public long getSize() {
        return byteSize;
    }
}