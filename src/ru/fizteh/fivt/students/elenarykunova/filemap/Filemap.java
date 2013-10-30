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
        
    protected int getDataBaseFromKeyAndCheck(String key) {
        int hashcode = Math.abs(key.hashCode());
        int ndir = hashcode % 16;
        int nfile = hashcode / 16 % 16;
        
        if (!data[ndir][nfile].hasFile()) {
            data[ndir][nfile] = new DataBase(currTablePath, ndir, nfile, updatedMap, true);
        }
        return ndir * 16 + nfile;
    }    
    
    public String getName() {
        return currTableName;
    }
    
    public String get(String key) throws IllegalArgumentException {
        if (key == null) {
            IllegalArgumentException e = new IllegalArgumentException("key is null");
            throw e;
        }
        String res = updatedMap.get(key);
        return res;
    }

    public String put(String key, String value) throws IllegalArgumentException {
        if (key == null || value == null) {
            IllegalArgumentException e = new IllegalArgumentException("key or value is null");
            throw e;
        }
        String res = updatedMap.put(key, value);
        return res;
    }

    public String remove(String key) throws IllegalArgumentException {
        if (key == null) {
            IllegalArgumentException e = new IllegalArgumentException("key is null");
            throw e;
        }
        String res = updatedMap.put(key, null);
        return res;
    }

    public int size() {
        int sz = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                sz += data[i][j].data.size();
            }
        }
        return sz;
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
                data[ndir][nfile].remove(key);
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
        
    public int commit() {
        int nchanges = getUncommitedChangesAndTrack(true);
        saveChanges();
        return nchanges;
    }
    
    public int rollback() {
        int nchanges = getUncommitedChangesAndTrack(false);
        load();
        return nchanges;
    }

    public void setNameToNull() {
        currTablePath = null;
        currTableName = null;
    }
    
    public void saveChanges() {
        if (currTableName == null) {
            return;
        }
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (data[i][j].hasFile()) {
                    try {
                        data[i][j].commitChanges();
                    } catch (IOException e) {
                        System.err.println("can't write to file");
                        data[i][j].closeDataFile();
                        System.exit(1);
                    }
                }
            }
        }
        Shell sh = new Shell(currTablePath);
        for (int i = 0; i < 16; i++) {
            File tmpDir = new File(currTablePath + File.separator + i + ".dir");
            if (tmpDir.exists() && tmpDir.list().length == 0) {
                sh.rm(tmpDir.getAbsolutePath());
            }
        }
    }

    public void load() {
        if (updatedMap != null) {
            updatedMap.clear();
        }
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                data[i][j] = new DataBase(currTablePath, i, j, updatedMap, false);
            }
        }
    }

    public Filemap(String path, String name) {
        currTablePath = path;
        currTableName = name;
        if (currTableName != null) {
            load();
        }
    }
}
