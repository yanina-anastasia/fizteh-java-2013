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
    public String tableName;
    DbState[][] data;
    public ShellState shell;
    private String rootPath;
    public boolean isDropped;
    public int changesNum;
    private int dbSize;
    
    public MultiDbState(String property) throws IllegalArgumentException {
        if (property == null || property.isEmpty()) {
            throw new IllegalArgumentException("wrong root directory");
        }
        
        changesNum = 0;
        dbSize = 0;
        isDropped = false;
        rootPath = new File(property).getAbsolutePath();
        data = new DbState[folderNum][fileInFolderNum];
        shell = new ShellState();
        shell.cd(rootPath);
        
        tableName = null;
        if (setCurrentDir() != 0) {
            throw new IllegalArgumentException(property + ": wrong root directory");
        }
        
        commands = new HashMap<String, Command>();
        this.add(new DbGet());
        this.add(new DbPut());
        this.add(new DbRemove());
        this.add(new MultiDbDrop());
        this.add(new MultiDbCreate());
        this.add(new MultiDbUse());
        this.add(new DbCommit());
        this.add(new DbRollback());
        this.add(new DbSize());
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
    }
    
    public boolean fileExist(String name) {
        return new File(makeNewSource(name)).exists();
    }
    
    public boolean isDbChosen() {
        return tableName != null;
    }
    
    private int setCurrentDir() {
        currentDir = new File(rootPath);
        if (!currentDir.exists() || !currentDir.isDirectory()) {
            return 1;
        } else {
            shell.cd(rootPath);
            return 0;
        }
    }
    
    public int changeBase(String name) {
        if (isDbChosen()) {
            commit();   
        }
        dbSize = 0;
        changesNum = 0;
        File lastDir = shell.currentDir;
        shell.currentDir = new File(makeNewSource(name));
        int result = shell.cd(makeNewSource(name));
        if (result == 0) {
            try {
                loadData();
            } catch (IOException e) {
                shell.currentDir = lastDir;
                System.err.println("multifilemap: loading data: " + e.getMessage());
                throw new IllegalArgumentException();
            } catch (DataFormatException e) {
                shell.currentDir = lastDir;
                System.err.println("multifilemap: " + e.getMessage());
                throw new IllegalArgumentException();
            }
        }
        
        if (result == 0) {
            tableName = name;
            isDropped = false;
        }
        return result;
    }
     
    @Override
    public int exitWithError(int errCode) throws DbExitException {
        if (!isDbChosen()) {
            throw new DbExitException(0);
        }
        int result = commit();
        if (result < 0) {
            errCode = 1;
        }
        
        throw new DbExitException(errCode);
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
        if (isDropped || !isDbChosen()) {
            return 0;
        }
        
        for (int i = 0; i < folderNum; i++) {
            String fold = Integer.toString(i) + ".dir";
            if (checkFolder(shell.makeNewSource(fold)) != 0) {
                throw new DataFormatException("wrong subfolder " + i);
            }
            for (int j = 0; j < fileInFolderNum; j++) {
                String file = Integer.toString(j) + ".dat";
                data[i][j].commit();
                if (data[i][j].data.isEmpty()) {
                    String[] arg = {shell.makeNewSource(fold, file)};
                    shell.rm(arg);
                }
                
                try {
                    data[i][j].dbFile.close();
                } catch (IOException e) {
                    throw new IOException("error in file closing");
                }
            }
           
            if (new File(shell.makeNewSource(fold)).listFiles().length == 0) {
                String[] arg = {fold};
                shell.rm(arg);
            }
        }
        int chNum = changesNum;
        changesNum = 0;
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
        if (key == null || value == null || key.trim() == null || value.trim() == null
                    || key.isEmpty() || value.isEmpty()) {
            throw new IllegalArgumentException();
        }

        if (!isDbChosen() || isDropped) {
            return null;
        }
        
        int folder = getFolderNum(key);
        int file = getFileNum(key);
        String result = data[folder][file].put(new String[] {key, value});
        if (result == null) {
            changesNum++;
            dbSize++;
        }
        return result;  
    }
    
    public String get(String key) {
        if (key == null || key.trim() == null || key.isEmpty()) {
            throw new IllegalArgumentException();
        }
        
        if (!isDbChosen() || isDropped) {
            return null;
        }
        
        int folder = getFolderNum(key);
        int file = getFileNum(key);
        return data[folder][file].get(new String[] {key});  
    }
    
    public String remove(String key) {
        if (key == null || key.trim() == null || key.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (!isDbChosen() || isDropped) {
            return null;
        }
        
        int folder = getFolderNum(key);
        int file = getFileNum(key);
        String result = data[folder][file].remove(new String[] {key});
        if (result != null) {
            dbSize--;
            changesNum++;
        }
        return result;  
    }
    
    public int size() {
        return dbSize;
    }
    
    public int rollback() {
        int chNum = changesNum;
        changesNum = 0;
        try {
            loadData();
        } catch (DataFormatException e) {
            System.err.println("database: wrong format");
        } catch (IOException e) {
            System.err.println("database: wrong format");
        }   
        return chNum;
    }
    
    public String getName() {
        return (shell.currentDir.getName());
    }
    
    public void use(String dbName) {
        if (changesNum > 0) {
            System.out.println(changesNum + " uncommited changes");
            return;
        }
        if (dbName == null || dbName.trim() == null || dbName.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (dbName.matches(".*" + File.separator + ".*") 
                || dbName.matches(".*\\.\\..*")) {
            throw new IllegalArgumentException();
        }
        if (fileExist(dbName)) {
            throw new DbReturnStatus(2);
        }
        
        changeBase(dbName);
        throw new DbReturnStatus(0);
    }
    
    public void create(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        
        if (fileExist(name)) {
            throw new DbReturnStatus(2);
        }
        
        name = makeNewSource(name);
        shell.mkdir(new String[] {name});
        throw new DbReturnStatus(0);
    }
}
