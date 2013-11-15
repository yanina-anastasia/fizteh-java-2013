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

public class Filemap implements Table {

    private DataBase[][] data = new DataBase[16][16];
    private String currTablePath = null;
    private String currTableName = null;
    private HashMap<String, Storeable> updatedMap = new HashMap<String, Storeable>();
    private MyTableProvider provider = null;
    private List<Class<?>> types = new ArrayList<Class<?>>();

    public HashMap<String, Storeable> getHashMap() {
        return updatedMap;
    }

    public MyTableProvider getProvider() {
        return provider;
    }

    public String getTablePath() {
        return currTablePath;
    }

    protected int getDataBaseFromKeyAndCheck(String key)
            throws RuntimeException {
        int hashcode = Math.abs(key.hashCode());
        int ndir = hashcode % 16;
        int nfile = hashcode / 16 % 16;

        if (!data[ndir][nfile].hasFile()) {
            try {
                data[ndir][nfile] = new DataBase(this, ndir, nfile, true);
            } catch (ParseException e) {
                throw new RuntimeException("wrong type (" + e.getMessage()
                        + ")", e);
            }
        }
        return ndir * 16 + nfile;
    }

    public String getName() {
        return currTableName;
    }

    public boolean isEmpty(String val) {
        return (val == null || (val.isEmpty() || val.trim().isEmpty()));
    }

    public boolean isCorrectKey(String key) {
        return (!provider.hasBadSymbols(key) && !key.contains("[") && !key
                .contains("]"));
    }

     public Storeable get(String key) throws IllegalArgumentException {
        if (isEmpty(key)) {
            throw new IllegalArgumentException("get: key is empty");
        }
        if (!isCorrectKey(key)) {
            throw new IllegalArgumentException("get: key has bad symbols");
        }
        Storeable res = updatedMap.get(key);
        return res;
    }

    private void checkValue(Storeable value) {
        for (int i = 0; i < types.size(); ++i) {
            try {
                Object val = value.getColumnAt(i);
                /*if (val != null && !types.get(i).equals(val.getClass())) {
                    if (val.getClass().equals(String.class)) {
                        String str = (String) val;
                        throw new ColumnFormatException("types mismatch: expected "
                                + types.get(i) + " but was " + val.getClass() + " " + str);                        
                    } else {
                        throw new ColumnFormatException("types mismatch: expected "
                                + types.get(i) + " but was " + val.getClass());
                    }
                }*/
                if (val != null && !types.get(i).equals(val.getClass())) {
                    String str = "";
                    for (int j = 0; j < types.size(); j++) {
                        str += types.get(j) + " ";
                    }
                    str += " but: ";
                    for (int j = 0; j < types.size(); j++) {
                        if (value.getColumnAt(j) != null) {
                            str += value.getColumnAt(j).getClass() + " ";
                        } else {
                            str += "null ";
                        }
                    }
                    throw new ColumnFormatException(str);                  
                    }
                if (types.get(i).equals(String.class) && val != null
                        && value.getStringAt(i) != null && value.getStringAt(i).trim().isEmpty()) {
                    throw new IllegalArgumentException("empty string in newValue");
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
            throw new ColumnFormatException("put: " + e1.getMessage(), e1);
        }
        Storeable res = updatedMap.put(key, value);
        return res;
    }

    public Storeable remove(String key) throws IllegalArgumentException {
        if (isEmpty(key)) {
            throw new IllegalArgumentException("remove: key is empty");
        }
        if (!isCorrectKey(key)) {
            throw new IllegalArgumentException("remove: key has bad symbols");
        }
        Storeable res = updatedMap.put(key, null);
        return res;
    }

    public int size() {
        int n = 0;
        Set<Map.Entry<String, Storeable>> mySet = updatedMap.entrySet();
        for (Map.Entry<String, Storeable> myEntry : mySet) {
            if (myEntry.getValue() != null) {
                n++;
            }
        }
        return n;
    }

    public int getUncommitedChangesAndTrack(boolean trackChanges) {
        Set<Map.Entry<String, Storeable>> mySet = updatedMap.entrySet();
        int k;
        int ndir;
        int nfile;
        int nchanges = 0;
        String key;
        String val;
        for (Map.Entry<String, Storeable> myEntry : mySet) {
            key = myEntry.getKey();
            k = getDataBaseFromKeyAndCheck(key);
            ndir = k / 16;
            nfile = k % 16;

            val = data[ndir][nfile].get(key);

            if (myEntry.getValue() == null) {
                if (data[ndir][nfile].remove(key) != null) {
                    nchanges++;
                }
            } else {
                String currVal = getProvider().serialize(this,
                        myEntry.getValue());
                if (val == null || !val.equals(currVal)) {
                    nchanges++;
                    if (trackChanges) {
                        try {
                            data[ndir][nfile].put(key, currVal);
                        } catch (ColumnFormatException e) {
                            throw new RuntimeException("some problems", e);
                        }
                    }
                }
            }
        }
        return nchanges;
    }

    public int commit() throws RuntimeException {
        int nchanges = getUncommitedChangesAndTrack(true);
        if (nchanges != 0) {
            try {
                saveChanges();
            } catch (RuntimeException e) {
                throw e;
            }
        }
        return nchanges;
    }

    public int rollback() throws RuntimeException {
        int nchanges = getUncommitedChangesAndTrack(false);
        if (nchanges != 0) {
            load();
        }
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
                if (data[i][j].hasFile()) {
                    try {
                        data[i][j].commitChanges();
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

    public void load() throws RuntimeException {
        if (updatedMap != null) {
            updatedMap.clear();
        }
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                try {
                    data[i][j] = new DataBase(this, i, j, false);
                } catch (ParseException e) {
                    throw new RuntimeException("wrong type (load "
                            + e.getMessage() + ")", e);
                }
            }
        }
    }

    public Filemap() {
    }

    public Filemap(String path, String name, MyTableProvider mtp,
            List<Class<?>> columnTypes) throws RuntimeException, IOException {
        provider = mtp;
        currTablePath = path;
        currTableName = name;
        types = new ArrayList<Class<?>>(columnTypes);

        if (currTableName != null) {
            load();
        }
    }

    @Override
    public int getColumnsCount() {
        return types.size();
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
}
