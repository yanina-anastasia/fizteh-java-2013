package ru.fizteh.fivt.students.elenarykunova.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.elenarykunova.shell.Shell;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import java.util.concurrent.locks.ReadWriteLock;
//import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyTable implements Table {

    private DataBase[][] data = new DataBase[16][16];
    private String currTablePath = null;
    private String currTableName = null;
    private MyTableProvider provider = null;
    private List<Class<?>> types = new ArrayList<Class<?>>();
    private HashMap<String, Storeable> changesMap = new HashMap<String, Storeable>();
    
/*    private ThreadLocal<HashMap<String, Storeable>> updatedMap = new ThreadLocal<HashMap<String, Storeable>>() {
        @Override
        protected HashMap<String, Storeable> initialValue() {
            return new HashMap<String, Storeable>();
        }
    };
*/
/*    public HashMap<String, Storeable> getHashMap() {
        return updatedMap;
    }*/

    public MyTableProvider getProvider() {
        return provider;
    }

    public String getTablePath() {
        return currTablePath;
    }

    protected DataBase getDataBaseFromKey(String key) throws RuntimeException {
        int hashcode = Math.abs(key.hashCode());
        int ndir = hashcode % 16;
        int nfile = hashcode / 16 % 16;
        return data[ndir][nfile];
    }

    public String getName() {
        return currTableName;
    }

    public boolean isEmpty(String val) {
        return (val == null || val.trim().isEmpty());
    }

    public boolean isCorrectKey(String key) {
        return (!provider.hasBadSymbols(key) && !key.contains("[") && !key
                .contains("]"));
    }

    public Storeable getWithoutChecking(String key) {
        if (changesMap.containsKey(key)) {
            return changesMap.get(key);
        } else {
            return getDataBaseFromKey(key).get(key);
        }
    }
    
    public Storeable get(String key) throws IllegalArgumentException {
        if (isEmpty(key)) {
            throw new IllegalArgumentException("get: key is empty");
        }
        return getWithoutChecking(key);
    }

    private void checkValue(Storeable value) {
        for (int i = 0; i < types.size(); ++i) {
            try {
                Object val = value.getColumnAt(i);
                if (val != null && !types.get(i).equals(val.getClass())) {
                    throw new ColumnFormatException("types mismatch: expected "
                            + types.get(i) + " but was " + val.getClass());
                }
            } catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException("number of columns mismatch");
            }
        }
        try {
            value.getColumnAt(types.size());
            throw new ColumnFormatException("number of columns mismatch");
        } catch (IndexOutOfBoundsException e) {
            return;
        }
    }

    private boolean differs(Storeable st1, Storeable st2) {
        String str1 = provider.serialize(this, st1);
        String str2 = provider.serialize(this, st2);
        return (!str1.equals(str2));
    }
    
    public Storeable put(String key, Storeable value)
            throws IllegalArgumentException, ColumnFormatException {
        if (isEmpty(key)) {
            throw new IllegalArgumentException("put: key is empty");
        }
        if (!isCorrectKey(key)) {
            throw new IllegalArgumentException("put: key has bad symbols");
        }
        if (value == null) {
            throw new IllegalArgumentException("put: value is empty");
        }
        try {
            checkValue(value);
        } catch (ColumnFormatException e1) {
            throw new ColumnFormatException("wrong type (put: "
                    + e1.getMessage() + ")", e1);
        }
        Storeable oldVal = getDataBaseFromKey(key).get(key);
        Storeable newVal = changesMap.get(key);
        Storeable res = null;
        if (changesMap.containsKey(key)) {
            res = newVal;
        } else if (oldVal != null) {
            res = oldVal;
        }
        if (oldVal != null) {
            if (differs(oldVal, value)) {
                    changesMap.put(key, value);
            } else {
                changesMap.remove(key);
            }
        } else {
            changesMap.put(key, value);
        }
        return res;
    }

    public Storeable remove(String key) throws IllegalArgumentException {
        if (isEmpty(key)) {
            throw new IllegalArgumentException("remove: key is empty");
        }
        Storeable res = getWithoutChecking(key);
        changesMap.put(key, null);
        if (getDataBaseFromKey(key).get(key) == null) {
            changesMap.remove(key);
        }
        return res;
    }

    public int size() {
        int n = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                n += data[i][j].getSize();
            }
        }
        Set<Map.Entry<String, Storeable>> mySet = changesMap.entrySet();
        for (Map.Entry<String, Storeable> myEntry : mySet) {
            if (getDataBaseFromKey(myEntry.getKey()).get(myEntry.getKey()) == null) {
                if (myEntry.getValue() != null) {
                    n++;
                }
            } else if (myEntry.getValue() == null) {
                n--;
            }
        }
        return n;
    }

/*    public int getUncommitedChangesAndTrack(boolean trackChanges)
            throws RuntimeException {
        Set<Map.Entry<String, Storeable>> mySet = updatedMap.entrySet();
        int nchanges = 0;
        String key;
        String val;
        for (Map.Entry<String, Storeable> myEntry : mySet) {
            key = myEntry.getKey();

            DataBase myDBFile = getDataBaseFromKey(key);
            if (myEntry.getValue() != null) {
                if (!myDBFile.hasFile()) {
                    myDBFile.createFile();
                }
            }

            val = myDBFile.get(key);

            if (myEntry.getValue() == null) {
                if (myDBFile.get(key) != null) {
                    nchanges++;
                }
                if (trackChanges) {
                    myDBFile.remove(key);
                    myDBFile.hasChanged = true;
                }
            } else {
                String currVal = getProvider().serialize(this,
                        myEntry.getValue());
                if (val == null || !val.equals(currVal)) {
                    nchanges++;
                    if (trackChanges) {
                        try {
                            myDBFile.put(key, currVal);
                            myDBFile.hasChanged = true;
                        } catch (ColumnFormatException e) {
                            throw new RuntimeException("some problems", e);
                        }
                    }
                }
            }
        }
        return nchanges;
    }*/

    public void trackChanges() {
        Set<Map.Entry<String, Storeable>> mySet = changesMap.entrySet();
        String key;
        Storeable value;
        DataBase mdb;
        for (Map.Entry<String, Storeable> myEntry : mySet) {
            key = myEntry.getKey();
            value = myEntry.getValue();
            mdb = getDataBaseFromKey(myEntry.getKey());
            if (value == null) {
                mdb.remove(key);
            } else {
                mdb.put(key, value);
            }
            mdb.createFile();
            mdb.hasChanged = true;
        }
    }
    
    public int commit() throws RuntimeException {
        int nchanges = getUncommitedChanges();
        trackChanges();
        if (nchanges != 0) {
            saveChanges();
            changesMap.clear();
        }
        return nchanges;
    }

    public int rollback() throws RuntimeException {
        int nchanges = getUncommitedChanges();
/*        if (nchanges != 0) {
            try {
                loadFromDataMap();
            } catch (ParseException | IllegalArgumentException e) {
                throw new RuntimeException(
                        "can't read data from saved changes", e);
            }
        }
        */
        changesMap.clear();
        return nchanges;
    }

    public void setNameToNull() {
        currTablePath = null;
        currTableName = null;
    }

    public void saveChanges() throws RuntimeException {
        if (currTableName == null) {
            return;
        }
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (data[i][j].hasFile() && data[i][j].hasChanged) {
                    try {
                        data[i][j].commitChanges();
                        data[i][j].hasChanged = false;
                    } catch (IOException e) {
                        throw new RuntimeException("can't write to file", e);
                    }
                }
            }
        }
        Shell sh = new Shell(currTablePath, false);
        for (int i = 0; i < 16; i++) {
            File tmpDir = new File(currTablePath + File.separator + i + ".dir");
            if (tmpDir.exists() && tmpDir.list().length == 0) {
                if (sh.rm(tmpDir.getAbsolutePath()) != Shell.ExitCode.OK) {
                    throw new RuntimeException(tmpDir.getAbsolutePath()
                            + " can't delete directory");
                }
            }
        }
    }

    public void loadFromDisk() throws RuntimeException {
        if (changesMap != null) {
            changesMap.clear();
        } else {
            changesMap = new HashMap<String, Storeable>();
        }
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                try {
                    data[i][j] = new DataBase(this, i, j);
                } catch (ParseException e) {
                    throw new RuntimeException("wrong type (load "
                            + e.getMessage() + ")", e);
                }
            }
        }
    }

    public void loadFromDataMap() throws IllegalArgumentException,
            ParseException {
        if (changesMap != null) {
            changesMap.clear();
        } else {
            changesMap = new HashMap<String, Storeable>();
        }

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (data[i][j].hasFile()) {
                    data[i][j].loadDataToMap(changesMap);
                }
            }
        }
    }

    public MyTable() {
    }

    public MyTable(String path, String name, MyTableProvider mtp,
            List<Class<?>> columnTypes) throws RuntimeException, IOException {
        provider = mtp;
        currTablePath = path;
        currTableName = name;
        types = new ArrayList<Class<?>>(columnTypes);
        if (currTableName != null) {
            loadFromDisk();
        }
    }

    @Override
    public int getColumnsCount() {
        return types.size();
    }

    public int getUncommitedChanges() {
        return changesMap.size();
    }
    
    @Override
    public Class<?> getColumnType(int columnIndex)
            throws IndexOutOfBoundsException {
        if (0 > columnIndex || columnIndex >= types.size()) {
            throw new IndexOutOfBoundsException(
                    "get column type: index is out of bounds");
        }
        return types.get(columnIndex);
    }

    public static void main(String[] args) {
        MyTableProviderFactory myFactory = new MyTableProviderFactory();
        MyTableProvider provider;
        try {
            provider = (MyTableProvider) myFactory.create(System.getProperty("fizteh.db.dir"));
            MyTable mp = new MyTable();
            ExecuteCmd exec = new ExecuteCmd(mp, provider);
            exec.workWithUser(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e1) {
            System.err.println(e1.getMessage());
            System.exit(1);
        } catch (IllegalStateException e2) {
            System.err.println(e2.getMessage());
            System.exit(1);
        } catch (RuntimeException e3) {
            System.err.println(e3.getMessage());
            System.exit(1);
        }
    }

}
