package ru.fizteh.fivt.students.elenarykunova.filemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.elenarykunova.shell.Shell;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Filemap implements Table {

    private DataBase[][] data = new DataBase[16][16];
    private String currTablePath = null;
    private String currTableName = null;
    public HashMap<String, Storeable> updatedMap = new HashMap<String, Storeable>();
    private MyTableProvider provider = null;
    List<Class<?>> types = null;

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
            } catch (RuntimeException e) {
                throw e;
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
        return (!provider.hasBadSymbols(key) && !key.contains("[") && !key.contains("]"));
    }

    public Storeable get(String key) throws IllegalArgumentException {
        if (isEmpty(key)) {
            throw new IllegalArgumentException("key is empty");
        }
        if (!isCorrectKey(key)) {
            throw new IllegalArgumentException("key has whitespaces");
        }
        Storeable res = updatedMap.get(key);
        return res;
    }

    public boolean isCorrectValue(Storeable value) throws ColumnFormatException {
        if (value == null) {
            return false;
        }
        
        int k = 0;
        while (k < types.size() + 1) {
            try {
                value.getColumnAt(k);
            } catch (IndexOutOfBoundsException e) {
                if (k != types.size()) {
                    throw new ColumnFormatException("number of columns mismatch");
                }
                break;
            }
            k++;
        }
        
        for (int i = 0; i < types.size(); i++) {
            if (!types.get(i).equals(value.getColumnAt(i).getClass())) {
                return false;
            }
        }
        return true;
    }
    
    public Storeable put(String key, Storeable value)
            throws IllegalArgumentException, ColumnFormatException {
        if (isEmpty(key)) {
            throw new IllegalArgumentException("key is empty");
        }
        if (!isCorrectKey(key)) {
            throw new IllegalArgumentException("key has bad symbols");
        }
        if (!isCorrectValue(value)) {
            throw new IllegalArgumentException("value is empty");
        }
        for (int i = 0; i < getColumnsCount(); i++) {
            if (!getColumnType(i).equals(value.getColumnAt(i).getClass())) {
                throw new ColumnFormatException("put: types mismatch");
            }
        }
        Storeable res = updatedMap.put(key, value);
        return res;
    }

    public Storeable remove(String key) throws IllegalArgumentException {
        if (isEmpty(key)) {
            throw new IllegalArgumentException("key is empty");
        }
        if (!isCorrectKey(key)) {
            throw new IllegalArgumentException("key has whitespaces");
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
                data[i][j] = new DataBase(this, i, j, false);
            }
        }
    }

    public Filemap() {
    }

    public Class<?> getTypeFromString(String type) throws IOException {
        switch (type) {
        case "int":
            return Integer.class;
        case "long":
            return Long.class;
        case "double":
            return Double.class;
        case "byte":
            return Byte.class;
        case "float":
            return Float.class;
        case "boolean":
            return Boolean.class;
        case "String":
            return String.class;
        default:
            throw new IOException(type + " types in signature.tsv mismatch");
        }

    }

    public Filemap(String path, String name, MyTableProvider mtp)
            throws RuntimeException, IOException {
        provider = mtp;
        currTablePath = path;
        currTableName = name;
        File info = new File(path + File.separator + "signature.tsv");
        types = new ArrayList();

        if (info.exists()) {
            FileInputStream is;
            is = new FileInputStream(info);
            try {
                Scanner sc = new Scanner(is);
                sc.useDelimiter(" ");
                try {
                    while (sc.hasNext()) {
                        String type = sc.next();
                        types.add(getTypeFromString(type));
                    } 
                } finally {
                    sc.close();
                }
            } finally {
                is.close();
            }
        } else {
            throw new RuntimeException("can't load data from table");
        }
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
