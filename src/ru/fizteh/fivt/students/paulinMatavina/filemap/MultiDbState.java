package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.DataFormatException;

import ru.fizteh.fivt.students.paulinMatavina.shell.ShellState;
import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class MultiDbState extends State{
    final int folderNum = 16;
    final int fileInFolderNum = 16;
    public String tableName;
    DbState[][] data;
    public ShellState shell;
    private String rootPath;
    public boolean isDropped;
    
    public MultiDbState() throws DataFormatException {
        String property = System.getProperty("fizteh.db.dir");
        if (property == null) {
            throw new DataFormatException("wrong root directory");
        }
        isDropped = false;
        rootPath = new File(property).getAbsolutePath();
        data = new DbState[folderNum][fileInFolderNum];
        shell = new ShellState();
        shell.cd(rootPath);
        
        tableName = null;
        if (setCurrentDir() != 0) {
            throw new DataFormatException(property + ": wrong root directory");
        }
        
        commands = new HashMap<String, Command>();
        this.add(new MultiDbGet());
        this.add(new MultiDbPut());
        this.add(new MultiDbRemove());
        this.add(new MultiDbDrop());
        this.add(new MultiDbCreate());
        this.add(new MultiDbUse());
        this.add(new DbExit());
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
        for (int i = 0; i < folderNum; i++) {
            String fold = Integer.toString(i) + ".dir";
            if (checkFolder(shell.makeNewSource(fold)) != 0) {
                throw new DataFormatException("wrong subfolder " + i);
            }
            for (int j = 0; j < fileInFolderNum; j++) {
                String file = Integer.toString(j) + ".dat";
                String filePath = shell.makeNewSource(fold, file);
                data[i][j] = new DbState(filePath);
                File f = new File(data[i][j].path);
                f.createNewFile();
                data[i][j].loadData();
            }
        }
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
            tryToCommit();   
        }
        File lastDir = shell.currentDir;
        
        int result = shell.cd(makeNewSource(name));
        if (result == 0) {
            try {
                loadData();
            } catch (IOException e) {
                shell.currentDir = lastDir;
                System.err.println("multifilemap: error in loading data");
                return 1;
            } catch (DataFormatException e) {
                shell.currentDir = lastDir;
                System.err.println("multifilemap: " + e.getMessage());
                return 1;
            }
        }
        
        if (result == 0) {
            tableName = name;
            isDropped = false;
        }
        return result;
    }
     
    @Override
    public void exitWithError(int errCode) {
        if (!isDbChosen()) {
            System.exit(0);
        }
        int result = tryToCommit();
        if (result != 0) {
            errCode = 1;
        }
        
        System.exit(errCode);
    }
    
    private int tryToCommit() {
        try {
            commit();
        } catch (IOException e) {
            System.out.println("multifilemap: error while writing data to the disk");
            return 1;
        } catch (DataFormatException e) {
            System.out.println("multifilemap: " + e.getMessage());
            return 1;
        }
        return 0;
    }
    
    public void commit() throws IOException, DataFormatException {
        if (isDropped) {
            return;
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
    }
    
    public int getFolderNum(String key) {
        byte[] bytes = key.getBytes();
        return (Math.abs(bytes[0]) % 16);
    }
    
    public int getFileNum(String key) {
        byte[] bytes = key.getBytes();
        return (Math.abs(bytes[0]) / 16 % 16);
    }
}
