package ru.fizteh.fivt.students.elenarykunova.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.elenarykunova.shell.Shell;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Filemap implements Table{

    private DataBase[][] data = new DataBase[16][16];
    private String currTablePath = null;
    private String currTableName = null;
    private HashMap<String, String> updatedMap = new HashMap<String, String>();
        
    protected int getDataBaseFromKeyAndCheck(String key) throws RuntimeException {
        int hashcode = Math.abs(key.hashCode());
        int ndir = hashcode % 16;
        int nfile = hashcode / 16 % 16;
        
        if (!data[ndir][nfile].hasFile()) {
            try {
                data[ndir][nfile] = new DataBase(currTablePath, ndir, nfile, updatedMap, true);
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
    
    public String get(String key) throws IllegalArgumentException {
        if (isEmpty(key)) {
            throw new IllegalArgumentException("key is empty");
        }
        String res = updatedMap.get(key);
        return res;
    }

    public String put(String key, String value) throws IllegalArgumentException {
        if (isEmpty(key)) {
            throw new IllegalArgumentException("key is empty");
        }
        if (isEmpty(value)) {
            throw new IllegalArgumentException("value is empty");
        }            
        String res = updatedMap.put(key, value);
        return res;
    }

    public String remove(String key) throws IllegalArgumentException {
        if (isEmpty(key)) {
            throw new IllegalArgumentException("key is empty");
        }
        String res = updatedMap.put(key, null);
        return res;
    }

    public int size() {
        int n = 0;
        Set<Map.Entry<String, String>> mySet = updatedMap.entrySet();
        for (Map.Entry<String, String> myEntry : mySet) {
            if (myEntry.getValue() != null) {
                n++;
            }
        }
        return n;
    }

    public int getUncommitedChangesAndTrack(boolean trackChanges) {
        Set<Map.Entry<String, String>> mySet = updatedMap.entrySet();
        int k;
        int ndir;
        int nfile;
        int nchanges = 0;
        String key;
        String val;
        for (Map.Entry<String, String> myEntry : mySet) {
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
                if (val == null || !val.equals(myEntry.getValue())) {
                    nchanges++;
                    if (trackChanges) {
                        data[ndir][nfile].put(key, myEntry.getValue());
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
            try {
                load();
            } catch (RuntimeException e) {
                throw e;
            }
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
                    } catch (RuntimeException e) {
                        throw e;
                    }
                }
            }
        }
        Shell sh = new Shell(currTablePath);
        for (int i = 0; i < 16; i++) {
            File tmpDir = new File(currTablePath + File.separator + i + ".dir");
            if (tmpDir.exists() && tmpDir.list().length == 0) {
                if (sh.rm(tmpDir.getAbsolutePath()) != Shell.ExitCode.OK) {
                    throw new RuntimeException(tmpDir.getAbsolutePath() + " can't delete directory");
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
                    data[i][j] = new DataBase(currTablePath, i, j, updatedMap, false);
                } catch (RuntimeException e) {
                    throw e;
                }
            }
        }
    }
    
    public Filemap() {
    }
    
    public Filemap(String path, String name) throws RuntimeException {
        currTablePath = path;
        currTableName = name;
        if (currTableName != null) {
            try {
                load();
            } catch (RuntimeException e) {
                throw e;
            }
        }
    }
}
