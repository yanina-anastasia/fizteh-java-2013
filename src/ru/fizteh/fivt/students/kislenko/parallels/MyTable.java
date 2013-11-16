package ru.fizteh.fivt.students.kislenko.parallels;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.kislenko.multifilemap.TwoLayeredString;
import ru.fizteh.fivt.students.kislenko.storeable.Utils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyTable implements Table {
    private String name;
    private MyTableProvider provider;
    private ArrayList<Class<?>> types;
    private HashMap<String, Storeable> storage;
    private boolean[][] globalUses;
    private long byteSize;
    private int revision;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private ThreadLocal<boolean[][]> uses = new ThreadLocal<boolean[][]>() {
        @Override
        public boolean[][] initialValue() {
            boolean[][] temp = new boolean[16][16];
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    temp[i][j] = false;
                }
            }
            return temp;
        }
    };
    private ThreadLocal<Integer> count = new ThreadLocal<Integer>() {
        @Override
        public Integer initialValue() {
            return 0;
        }
    };
    private ThreadLocal<Integer> threadRevision = new ThreadLocal<Integer>() {
        @Override
        public Integer initialValue() {
            return 0;
        }
    };
    private ThreadLocal<HashMap<String, Storeable>> changes = new ThreadLocal<HashMap<String, Storeable>>() {
        @Override
        public HashMap<String, Storeable> initialValue() {
            return new HashMap<String, Storeable>();
        }
    };

    public MyTable(String tableName, List<Class<?>> columnTypes, MyTableProvider parent) {
        name = tableName;
        provider = parent;
        storage = new HashMap<String, Storeable>();
        byteSize = 0;
        revision = 0;
        globalUses = new boolean[16][16];
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                globalUses[i][j] = false;
            }
        }
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
        if (key.trim().isEmpty() || key.matches("(.+\\s+.+)+")) {
            throw new IllegalArgumentException("Incorrect key to get.");
        }
        lock.writeLock().lock();
        resetTable();
        if (changes.get().containsKey(key)) {
            lock.writeLock().unlock();
            return changes.get().get(key);
        }
        Storeable value = storage.get(key);
        lock.writeLock().unlock();
        return value;
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Incorrect key/value to put.");
        }
        if (key.trim().isEmpty() || key.matches("(.+\\s+.+)+")) {
            throw new IllegalArgumentException("Incorrect key to put.");
        }
        try {
            for (int i = 0; i < types.size(); ++i) {
                if (value.getColumnAt(i) != null && !types.get(i).equals(value.getColumnAt(i).getClass())) {
                    throw new ColumnFormatException("Incorrect value to put.");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("Incorrect value to put.");
        }
        if (!tryToGetUnnecessaryColumn(value)) {
            throw new ColumnFormatException("Incorrect value to put.");
        }
        lock.writeLock().lock();
        resetTable();
        if ((!changes.get().containsKey(key) && !storage.containsKey(key)) ||
                (changes.get().containsKey(key) && changes.get().get(key) == null)) {
            count.set(count.get() + 1);
        }
        TwoLayeredString twoLayeredKey = new TwoLayeredString(key);
        uses.get()[Utils.getDirNumber(twoLayeredKey)][Utils.getFileNumber(twoLayeredKey)] = true;
        Storeable v = get(key);
        String copyOfKey = "".concat(key);
        Storeable copyOfValue = provider.createFor(this);
        for (int i = 0; i < types.size(); ++i) {
            copyOfValue.setColumnAt(i, value.getColumnAt(i));
        }
        changes.get().put(copyOfKey, copyOfValue);
        if (storage.get(key) != null &&
                provider.serialize(this, value).equals(provider.serialize(this, storage.get(key)))) {
            changes.get().remove(key);
        }
        lock.writeLock().unlock();
        return v;
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Incorrect key to remove.");
        }
        if (key.trim().isEmpty() || key.matches("(.+\\s+.+)+")) {
            throw new IllegalArgumentException("Incorrect key to remove.");
        }
        lock.writeLock().lock();
        resetTable();
        if (changes.get().get(key) != null || (!changes.get().containsKey(key) && storage.get(key) != null)) {
            count.set(count.get() - 1);
        }
        TwoLayeredString twoLayeredKey = new TwoLayeredString(key);
        uses.get()[Utils.getDirNumber(twoLayeredKey)][Utils.getFileNumber(twoLayeredKey)] = true;
        Storeable v = get(key);
        changes.get().put(key, null);
        if (storage.get(key) == null) {
            changes.get().remove(key);
        }
        lock.writeLock().unlock();
        return v;
    }

    @Override
    public int size() {
        lock.writeLock().lock();
        resetTable();
        int tableSize = count.get();
        lock.writeLock().unlock();
        return tableSize;
    }

    @Override
    public int commit() throws IOException {
        lock.writeLock().lock();
        resetTable();
        for (String key : changes.get().keySet()) {
            if (changes.get().get(key) == null) {
                storage.remove(key);
            } else {
                storage.put(key, changes.get().get(key));
            }
        }
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                globalUses[i][j] = uses.get()[i][j];
            }
        }
        int n = changes.get().size();
        changes.get().clear();
        revision++;
        threadRevision.set(revision);
        lock.writeLock().unlock();
        return n;
    }

    @Override
    public int rollback() {
        lock.writeLock().lock();
        resetTable();
        int n = changes.get().size();
        changes.get().clear();
        count.set(storage.size());
        lock.writeLock().unlock();
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
        return uses.get()[nDirectory][nFile];
    }

    public void clear() {
        storage.clear();
        changes.get().clear();
    }

    public HashMap<String, Storeable> getMap() {
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
        return changes.get().size();
    }

    public MyTableProvider getProvider() {
        return provider;
    }


    private boolean tryToGetUnnecessaryColumn(Storeable value) {
        try {
            value.getColumnAt(types.size());
        } catch (IndexOutOfBoundsException e) {
            return true;
        }
        return false;
    }

    private void resetTable() {
        if (revision == threadRevision.get()) {
            return;
        }
        threadRevision.set(revision);
        count.set(storage.size());
        HashMap<String, Storeable> tempMap = new HashMap<String, Storeable>();
        for (String key : changes.get().keySet()) {
            if (changes.get().get(key) == null && !storage.containsKey(key)) {
                changes.get().remove(key);
            } else if (changes.get().get(key) == null && storage.containsKey(key)) {
                tempMap.put(key, null);
                count.set(count.get() - 1);
            } else if (!storage.containsKey(key)) {
                tempMap.put(key, changes.get().get(key));
                count.set(count.get() + 1);
            } else if (storage.containsKey(key) && !(storage.get(key) != null &&
                    provider.serialize(this, changes.get().get(key)).equals(provider.serialize(this, storage.get(key))))) {
                tempMap.put(key, changes.get().get(key));
            }
        }
        changes.set(tempMap);
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                uses.get()[i][j] = globalUses[i][j];
            }
        }
    }
}
