package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import ru.fizteh.fivt.students.ryabovaMaria.shell.AbstractCommands;
import ru.fizteh.fivt.students.ryabovaMaria.shell.ShellCommands;

public class FileMapCommands extends AbstractCommands {
    public HashMap<String, String> list = new HashMap<String, String>();
    public boolean usingTable = false;
    public ShellCommands shellCommands;
    public File tableFile;
    int hashCode;
    int numberOfDir;
    int numberOfFile;
            
    public FileMapCommands(File curDir) {
        currentDir = curDir;
        shellCommands = new ShellCommands();
        shellCommands.currentDir = currentDir;
    }
    
    public void create() throws Exception {
        String tableName = lexems[1];
        File newFile = currentDir.toPath().resolve(tableName).normalize().toFile();
        if (newFile.exists()) {
            System.out.println(tableName + " exists");
        } else {
            if (!newFile.mkdir()) {
                throw new Exception("create: I can't create the file");
            } else {
                System.out.println("created");
            }
        }
    }
    
    public void drop() throws Exception {
        try {
            shellCommands.lexems = lexems;
            shellCommands.rm();
            System.out.println("dropped");
        } catch (Exception e) {
            if (e.getMessage().endsWith("mv: there is no enough arguments.")) {
                throw new Exception("drop: there is no enough arguments.");
            }
            if (e.getMessage().endsWith("mv: there is so many arguments.")) {
                throw new Exception("drop: there is so many arguments.");
            }
            System.out.println(lexems[1] + " not exists");
        }
    }
    
    public void isCorrect(File tempTableFile) throws Exception {
        String[] listOfDirs = tempTableFile.list();
        for (int i = 0; i < listOfDirs.length; ++i) {
            boolean ok = false;
            for (int j = 0; j < 16; ++j) {
                String validName = String.valueOf(j) + ".dir";
                if (listOfDirs[i].equals(validName)) {
                    ok = true;
                    break;
                }
            }
            if (ok) {
                File curDirPath = tempTableFile.toPath().resolve(listOfDirs[i]).toFile();
                String[] listOfFiles = curDirPath.list();
                for (int j = 0; j < listOfFiles.length; ++j) {
                    boolean okFile = false;
                    for (int k = 0; k < 16; ++k) {
                        String validName = String.valueOf(k) + ".dat";
                        if (listOfFiles[j].equals(validName)) {
                            okFile = true;
                            break;
                        }
                    }
                    if (!okFile) {
                        throw new Exception("Incorrect table");
                    }
                }
            } else {
                throw new Exception("Incorrect table");
            }
        }
    }
    
    public void use() throws Exception {
        String tableName = lexems[1];
        File tempTableFile = currentDir.toPath().resolve(tableName).normalize().toFile();
        if (!tempTableFile.exists()) {
            System.out.println(tableName + " not exists");
        } else {
            if (!tempTableFile.isDirectory()) {
                System.out.println(tableName + " not exists");
            } else {
                try {
                    isCorrect(tempTableFile);
                } catch (Exception e) {
                    throw new Exception("Incorrect table");
                }
                tableFile = tempTableFile;
                usingTable = true;
                System.out.println("using " + tableName);
            }
        }
    }
    
    public void loadFile() throws Exception {
        String dirString = String.valueOf(numberOfDir) + ".dir";
        String fileString = String.valueOf(numberOfFile) + ".dat";
        File dbFile = tableFile.toPath().resolve(dirString).resolve(fileString).normalize().toFile();
        if (!dbFile.exists()) {
            throw new FileNotFoundException();
        }
        if (!dbFile.isFile()) {
            throw new Exception("db.dat is not a file");
        }
        RandomAccessFile db;
        db = new RandomAccessFile(dbFile, "r");
        try {
            long curPointer = 0;
            long lastPointer = 0;
            long length = db.length();
            if (length == 0) {
                return;
            }
            db.seek(0);
            String lastKey = "";
            int lastOffset = 0;
            while (curPointer < length) {
                byte curByte = db.readByte();
                if (curByte == '\0') {
                    byte[] byteKey = new byte[(int) curPointer - (int) lastPointer];
                    curPointer = db.getFilePointer();
                    db.seek(lastPointer);
                    db.readFully(byteKey);
                    db.seek(curPointer);
                    String currentKey = new String(byteKey, "UTF-8");
                    int currentHashCode = Math.abs(currentKey.hashCode());
                    int currentNumOfDir = currentHashCode % 16;
                    int currentNumOfFile = currentHashCode / 16 % 16;
                    if (currentNumOfDir != numberOfDir || currentNumOfFile != numberOfFile) {
                        throw new Exception("Incorrect file");
                    }
                    int offset = db.readInt();
                    if (!lastKey.isEmpty()) {
                        byte[] byteValue = new byte[offset - lastOffset];
                        curPointer = db.getFilePointer();
                        db.seek(lastOffset);
                        db.readFully(byteValue);
                        String lastValue = new String(byteValue, "UTF-8");
                        db.seek(curPointer);
                        if (list.containsKey(lastKey)) {
                            System.err.println(lastKey + " meets twice in db.dat");
                            System.exit(1);
                        }
                        list.put(lastKey, lastValue);
                    }
                    lastOffset = offset;
                    lastKey = currentKey;
                    lastPointer = db.getFilePointer();
                }
                curPointer = db.getFilePointer();
            }
            if (lastOffset == 0 || lastKey.isEmpty()) {
                System.err.println("Incorrect db");
                System.exit(1);
            }
            byte[] byteValue = new byte[(int) length - lastOffset];
            db.seek(lastOffset);
            db.readFully(byteValue);
            String lastValue = new String(byteValue, "UTF-8");
            if (list.containsKey(lastKey)) {
                System.err.println(lastKey + " meets twice in db.dat");
                System.exit(1);
            }
            list.put(lastKey, lastValue);
        } catch (Exception e) {
            db.close();
            throw new Exception(e);
        }
        db.close();
    }
    
    public void writeIntoFile() throws Exception {
        String dirString = String.valueOf(numberOfDir) + ".dir";
        String fileString = String.valueOf(numberOfFile) + ".dat";
        File dbDir = tableFile.toPath().resolve(dirString).normalize().toFile();
        if (!dbDir.isDirectory()) {
            dbDir.mkdir();
        }
        File dbFile = dbDir.toPath().resolve(fileString).normalize().toFile();
        if (list.isEmpty()) {
            dbFile.delete();
            if (dbDir.list().length == 0) {
                dbDir.delete();
            }
            return;
        }
        RandomAccessFile db;
        db = new RandomAccessFile(dbFile, "rw");
        try {
            db.setLength(0);
            Iterator<Map.Entry<String, String>> it;
            it = list.entrySet().iterator();
            long[] pointers = new long[list.size()];
            int counter = 0;
            while (it.hasNext()) {
                Map.Entry<String, String> m = (Map.Entry<String, String>) it.next();
                String key = m.getKey();
                db.write(key.getBytes("UTF-8"));
                db.write("\0".getBytes("UTF-8"));
                pointers[counter] = db.getFilePointer();
                db.seek(pointers[counter] + 4);
                ++counter;
            }
            it = list.entrySet().iterator();
            counter = 0;
            while (it.hasNext()) {
                Map.Entry<String, String> m = (Map.Entry<String, String>) it.next();
                String value = m.getValue();
                int curPointer = (int) db.getFilePointer();
                db.seek(pointers[counter]);
                db.writeInt(curPointer);
                db.seek(curPointer);
                db.write(value.getBytes("UTF-8"));
                ++counter;
            }
        } catch (Exception e) {
            db.close();
            throw new Exception(e);
        }
        db.close();
        if (dbDir.list().length == 0) {
            dbDir.delete();
        }
    }
    
    private void parse() {
        String[] tempLexems = new String[0];
        if (lexems.length > 1) {
            tempLexems = lexems[1].split("[ \t\n\r]+", 2);
        }
        lexems = tempLexems;
    }
    
    private void checkTheNumbOfArgs(int n, String commandName) throws Exception {
        if (lexems.length < n) {
            throw new Exception(commandName + ": there is no enough arguments.");
        }
        if (lexems.length > n) {
            throw new Exception(commandName + ": there is so many arguments.");
        }
    }
    
    public void load(String commandName) throws Exception {
        hashCode = Math.abs(lexems[0].hashCode());
        numberOfDir = hashCode % 16;
        numberOfFile = hashCode / 16 % 16;
        list.clear();
        try {
            loadFile();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            throw new Exception(commandName + ": incorrect table");
        }
    }
       
    public void put() throws Exception {
        if (!usingTable || !tableFile.exists()) {
            System.out.println("no table");
            return;
        }
        parse();
        checkTheNumbOfArgs(2, "put");
        load("put");
        if (list.containsKey(lexems[0])) {
            String oldValue = list.get(lexems[0]);
            list.remove(lexems[0]);
            list.put(lexems[0], lexems[1]);
            System.out.println("overwrite");
            System.out.println(oldValue);
        } else {
            list.put(lexems[0], lexems[1]);
            System.out.println("new");
        }
        try {
            writeIntoFile();
        } catch (Exception e) {
            System.err.println("I can't write into file");
            System.exit(1);
        }    
    }
    
    public void get() throws Exception {
        if (!usingTable || !tableFile.exists()) {
            System.out.println("no table");
            return;
        }
        parse();
        checkTheNumbOfArgs(1, "get");
        load("get");
        if (list.containsKey(lexems[0])) {
            String value = list.get(lexems[0]);
            System.out.println("found");
            System.out.println(value);
        } else {
            System.out.println("not found");
        }
    }
    
    public void remove() throws Exception {
        if (!usingTable || !tableFile.exists()) {
            System.out.println("no table");
            return;
        }
        parse();
        checkTheNumbOfArgs(1, "remove");
        load("remove");
        if (list.containsKey(lexems[0])) {
            list.remove(lexems[0]);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        try { 
            writeIntoFile();
        } catch (Exception e) {
            System.err.println("I can't write into file");
            System.exit(1);
        }
    }
    
    public void exit() {
        System.exit(0);
    }
}
