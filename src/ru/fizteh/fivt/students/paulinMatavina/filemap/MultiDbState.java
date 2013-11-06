package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.DataFormatException;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.paulinMatavina.shell.ShellState;
import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class MultiDbState extends State implements Table {
    final int folderNum = 16;
    final int fileInFolderNum = 16;
    private String tableName;
    DbState[][] data;
    public ShellState shell;
    private String rootPath;
    public boolean isDropped;
    private int dbSize;
    private int primaryDbSize;
    
    public MultiDbState(String property, String dbName) {
        if (property == null || property.trim().isEmpty()) {
            throw new IllegalArgumentException("empty root directory");
        }

        dbSize = 0;
        isDropped = false;
        rootPath = property;
        data = new DbState[folderNum][fileInFolderNum];
        shell = new ShellState();
        currentDir = new File(rootPath);
        if (!currentDir.exists() || !currentDir.isDirectory()) {
            throw new IllegalArgumentException("wrong root directory " + rootPath);
        }
        shell.cd(rootPath);
        shell.cd(dbName);
        
        tableName = dbName;
        try {
            loadData();
        } catch (IOException e) {
            throw new IllegalArgumentException("wrong database file " + dbName);
        } catch (DataFormatException e) {
            throw new IllegalArgumentException("wrong database file " + dbName);
        }
    }
    
    private int checkFolder(String path) {
        File f = new File(path);
        if (!f.exists()) {
            String[] args = {path};
            int result = shell.mkdir(args);
            return result;
        }
        
        if (!f.isDirectory()) {
            return 1;
        }
        return 0;
    }
    
    private void loadData() throws IOException, DataFormatException {
        dbSize = 0;
        data = new DbState[folderNum][fileInFolderNum];
        for (int i = 0; i < folderNum; i++) {
            String fold = Integer.toString(i) + ".dir";
            if (checkFolder(shell.makeNewSource(fold)) != 0) {
                throw new DataFormatException("wrong subfolder " + i);
            }
            for (int j = 0; j < fileInFolderNum; j++) {
                String file = Integer.toString(j) + ".dat";
                String filePath = shell.makeNewSource(fold, file);
                data[i][j] = new DbState(filePath, i, j);
                File f = new File(data[i][j].path);
                f.createNewFile();
                dbSize += data[i][j].loadData();
            }
        }
        primaryDbSize = dbSize;
    }
    
    public void dropped() {
        isDropped = true;
    }
    
    public int commit() {
        try {
            return tryToCommit();
        } catch (IOException e) {
            System.out.println("multifilemap: error while writing data to the disk");
            return 0;
        } catch (DataFormatException e) {
            System.out.println("multifilemap: " + e.getMessage());
            return 0;
        }
    }
    
    private int tryToCommit() throws IOException, DataFormatException {
        if (isDropped) {
            return 0;
        }
        
        int chNum = changesNum();
        for (int i = 0; i < folderNum; i++) {
            String fold = Integer.toString(i) + ".dir";
            if (checkFolder(shell.makeNewSource(fold)) != 0) {
                throw new DataFormatException("wrong subfolder " + i);
            }
            for (int j = 0; j < fileInFolderNum; j++) {
                String file = Integer.toString(j) + ".dat";
                if (data[i][j].data.isEmpty() && new File(shell.makeNewSource(fold, file)).exists()) {
                    String[] arg = {shell.makeNewSource(fold, file)};
                    shell.rm(arg);
                } else {
                    data[i][j].commit();
                }
            }
           
            if (new File(shell.makeNewSource(fold)).listFiles().length == 0) {
                String[] arg = {fold};
                shell.rm(arg);
            }
        }
        
        primaryDbSize = dbSize;
        return chNum;
    }
    
    private int getFolderNum(String key) {
        byte[] bytes = key.getBytes();
        return (Math.abs(bytes[0]) % 16);
    }
    
    private int getFileNum(String key) {
        byte[] bytes = key.getBytes();
        return (Math.abs(bytes[0]) / 16 % 16);
    }
    
    public String put(String key, String value) { 
        validate(key);
        validate(value);

        if (isDropped) {
            return null;
        }
        
        int folder = getFolderNum(key);
        int file = getFileNum(key);
        String result = data[folder][file].put(new String[] {key, value});
        if (result == null) {
            dbSize++;
        }
        return result;  
    }
    
    public String get(String key) {
        validate(key);
        
        if (isDropped) {
            return null;
        }
        
        int folder = getFolderNum(key);
        int file = getFileNum(key);
        return data[folder][file].get(new String[] {key});  
    }   
    
    public String remove(String key) {
        validate(key);
        if (isDropped) {
            return null;
        }
        
        int folder = getFolderNum(key);
        int file = getFileNum(key);
        String result = data[folder][file].remove(new String[] {key});
        if (result != null) {
            dbSize--;
        }
        return result;  
    }
    
    public int size() {
        int result = 0;
        for (int i = 0; i < folderNum; i++) {
            for (int j = 0; j < fileInFolderNum; j++) {
                result += data[i][j].size();
            }
        }
        return result;
    }
    
    private void assignInit() {
        for (int i = 0; i < folderNum; i++) {
            for (int j = 0; j < fileInFolderNum; j++) {
                data[i][j].data = new HashMap<String, String>(data[i][j].initial);
            }
        }
    }
    
    public int rollback() {
        int chNum = changesNum();
        dbSize = primaryDbSize;
        assignInit();
        return chNum;
    }
    
    public String getName() {
        return tableName;
    }
    
    private void validate(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("empty parameter");
        }
    }
    
    public int changesNum() {
        int result = 0;
        for (int i = 0; i < folderNum; i++) {
            for (int j = 0; j < fileInFolderNum; j++) {
                result += data[i][j].getChangeNum();
            }
        } 
        return result;
    }
}
