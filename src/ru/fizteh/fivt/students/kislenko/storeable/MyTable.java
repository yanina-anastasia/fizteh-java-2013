package ru.fizteh.fivt.students.kislenko.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.kislenko.multifilemap.TwoLayeredString;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyTable implements Table {
    private String name;
    private MyTableProvider provider;
    private ArrayList<Class<?>> types;
    private HashMap<String, Value> storage;
    private HashMap<String, Value> changes;
    private boolean[][] uses;
    private long byteSize;
    private int count;

    public MyTable(String tableName, List<Class<?>> columnTypes, MyTableProvider parent) {
        name = tableName;
        provider = parent;
        storage = new HashMap<String, Value>();
        changes = new HashMap<String, Value>();
        byteSize = 0;
        uses = new boolean[16][16];
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j)
                uses[i][j] = false;
        }
        count = storage.size();
        types = new ArrayList<Class<?>>(columnTypes);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Incorrect key to get.");
        }
        if (key.trim().isEmpty() || key.split("\\s").length > 1) {
            throw new IllegalArgumentException("Incorrect key to get.");
        }
        if (changes.containsKey(key)) {
            return changes.get(key);
        }
        return storage.get(key);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Incorrect key/value to put.");
        }
        if (key.trim().isEmpty() || key.split("\\s").length > 1) {
            throw new IllegalArgumentException("Incorrect key/value to put.");
        }
//        for (int i = 0; i < types.size(); ++i) {
//            if (value.getColumnAt(i) == null) {
//                throw new IllegalArgumentException("Null value is not correct value.");
//            }
//        }
        for (int i = 0; i < types.size(); ++i) {
            if (value.getColumnAt(i) != null && !types.get(i).equals(value.getColumnAt(i).getClass())) {
                throw new ColumnFormatException("");
            }
        }
        if ((!changes.containsKey(key) && !storage.containsKey(key)) ||
                (changes.containsKey(key) && changes.get(key) == null)) {
            ++count;
        }
        TwoLayeredString twoLayeredKey = new TwoLayeredString(key);
        uses[Utils.getDirNumber(twoLayeredKey)][Utils.getFileNumber(twoLayeredKey)] = true;
        Storeable v = get(key);
        changes.put(key, (Value) value);
        if (value.equals(storage.get(key))) {
            changes.remove(key);
        }
        return v;
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Incorrect key to remove.");
        }
        if (key.trim().isEmpty() || key.split("\\s+").length > 1) {
            throw new IllegalArgumentException("Incorrect key to remove.");
        }
        if (changes.get(key) != null || (!changes.containsKey(key) && storage.get(key) != null)) {
            --count;
        }
        TwoLayeredString twoLayeredKey = new TwoLayeredString(key);
        uses[Utils.getDirNumber(twoLayeredKey)][Utils.getFileNumber(twoLayeredKey)] = true;
        Storeable v = get(key);
        changes.put(key, null);
        if (storage.get(key) == null) {
            changes.remove(key);
        }
        return v;
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public int commit() throws IOException {
        for (String key : changes.keySet()) {
            if (changes.get(key) == null) {
                storage.remove(key);
            } else {
                storage.put(key, changes.get(key));
            }
        }
        int n = changes.size();
        changes.clear();
        return n;
    }

    @Override
    public int rollback() {
        int n = changes.size();
        changes.clear();
        count = storage.size();
        return n;
    }

    @Override
    public int getColumnsCount() {
        return types.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex > types.size()) {
            throw new IndexOutOfBoundsException("Incorrect column number.");
        }
        return types.get(columnIndex);
    }

    public boolean isUsing(int nDirectory, int nFile) {
        return uses[nDirectory][nFile];
    }

    public void clear() {
        storage.clear();
        changes.clear();
    }

    public HashMap<String, Value> getMap() {
        return storage;
    }

    public Path getPath() {
        return Paths.get(name);
    }

    public void setByteSize(long newSize) {
        byteSize = newSize;
    }

    public long getByteSize() {
        return byteSize;
    }

    public int getChangeCount() {
        return changes.size();
    }

    public MyTableProvider getProvider() {
        return provider;
    }
}
