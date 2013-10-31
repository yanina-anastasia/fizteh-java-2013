package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import ru.fizteh.fivt.storage.strings.Table;

public class TableCommands implements Table {
    private File tableDir;
    private HashMap<String, String>[][] list;
    private HashMap<String, String>[][] lastList;
    private HashMap<Integer, String> update;
    private int hashCode;
    private int numberOfDir;
    private int numberOfFile;
    
    TableCommands(File directory) {
        list = new HashMap[16][16];
        lastList = new HashMap[16][16];
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                list[i][j] = new HashMap<String, String>();
                lastList[i][j] = new HashMap<String, String>();
            }
        }
        tableDir = directory;
        update = new HashMap<Integer, String>();
        isCorrectTable();
    }
    
    private void isCorrectTable() {
        String[] listOfDirs = tableDir.list();
        for (int i = 0; i < listOfDirs.length; ++i) {
            boolean ok = false;
            int value = -1;
            for (int j = 0; j < 16; ++j) {
                String validName = String.valueOf(j) + ".dir";
                if (listOfDirs[i].equals(validName)) {
                    ok = true;
                    value = j;
                    break;
                }
            }
            if (ok) {
                isCorrectDir(value, listOfDirs[i]);
            } else {
                throw new IllegalArgumentException("Incorrect table");
            }
        }
    }
    
    private void isCorrectDir(int numOfDir, String name) {
        File curDir = tableDir.toPath().resolve(name).normalize().toFile();
        String[] listOfFiles = curDir.list();
        for (int i = 0; i < listOfFiles.length; ++i) {
            boolean ok = false;
            int value = -1;
            for (int j = 0; j < 16; ++j) {
                String validName = String.valueOf(j) + ".dat";
                if (listOfFiles[i].equals(validName)) {
                    ok = true;
                    value = j;
                    break;
                }
            }
            if (ok) {
                isCorrectFile(numOfDir, value, curDir, listOfFiles[i]);
            } else {
                throw new IllegalArgumentException("Incorrect table");
            }
        }
    }
    
    private void isCorrectFile(int numOfDir, int numOfFile, File current, String name) {
        File dbFile = current.toPath().resolve(name).normalize().toFile();
        if (!dbFile.exists()) {
            throw new IllegalArgumentException("Incorrect table");
        }
        if (!dbFile.isFile()) {
            throw new IllegalArgumentException("Incorrect table");
        }
        RandomAccessFile db;
        try {
            db = new RandomAccessFile(dbFile, "rw");
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
                        if (currentNumOfDir != numOfDir || currentNumOfFile != numOfFile) {
                            throw new IllegalArgumentException("Incorrect file " + dbFile.toString());
                        }
                        int offset = db.readInt();
                        if (!lastKey.isEmpty()) {
                            byte[] byteValue = new byte[offset - lastOffset];
                            curPointer = db.getFilePointer();
                            db.seek(lastOffset);
                            db.readFully(byteValue);
                            String lastValue = new String(byteValue, "UTF-8");
                            db.seek(curPointer);
                            if (list[numOfDir][numOfFile].containsKey(lastKey)) {
                                System.err.println(lastKey + " meets twice in db.dat");
                                System.exit(1);
                            }
                            list[numOfDir][numOfFile].put(lastKey, lastValue);
                        }
                        lastOffset = offset;
                        lastKey = currentKey;
                        lastPointer = db.getFilePointer();
                    }
                    curPointer = db.getFilePointer();
                }
                if (lastOffset == 0 || lastKey.isEmpty()) {
                    throw new IllegalArgumentException("Incorrect file " + dbFile.toString());
                }
                byte[] byteValue = new byte[(int) length - lastOffset];
                db.seek(lastOffset);
                db.readFully(byteValue);
                String lastValue = new String(byteValue, "UTF-8");
                if (list[numOfDir][numOfFile].containsKey(lastKey)) {
                    throw new IllegalArgumentException("Incorrect file" + dbFile.toString());
                }
                list[numOfDir][numOfFile].put(lastKey, lastValue);
            } catch (Exception e) {
                db.close();
                throw new IllegalArgumentException("Incorrect table");
            }
        } catch(Exception e) {
            throw new IllegalArgumentException("Incorrect table");
        }
    }
    
    @Override
    public String getName() {
        return tableDir.getName();
    }

    private void getUsingDatFile(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Bad key");
        }
        hashCode = Math.abs(key.hashCode());
        numberOfDir = hashCode % 16;
        numberOfFile = hashCode / 16 % 16;
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Bad key");
        }
        getUsingDatFile(key);
        return list[numberOfDir][numberOfFile].get(key);
    }

    @Override
    public String put(String key, String value) {
        if (value == null || key == null) {
            throw new IllegalArgumentException("Bad args");
        }
        if (value.trim().isEmpty() || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Bad args");
        }
        getUsingDatFile(key);
        update.put(numberOfDir*16 + numberOfFile, " ");
        String lastValue = (String) list[numberOfDir][numberOfFile].put(key, value);
        return lastValue;
    }

    @Override
    public String remove(String key) {
        getUsingDatFile(key);
        update.put(numberOfDir*16 + numberOfFile, null);
        return list[numberOfDir][numberOfFile].remove(key);
    }

    @Override
    public int size() {
        int countSize = 0;
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                countSize += list[i][j].size();
            }
        }
        return countSize; 
    }
    
    private void writeIntoFile(int numOfDir, int numOfFile) {
        String dirString = String.valueOf(numOfDir) + ".dir";
        String fileString = String.valueOf(numOfFile) + ".dat";
        File dbDir = tableDir.toPath().resolve(dirString).normalize().toFile();
        if (!dbDir.isDirectory()) {
            dbDir.mkdir();
        }
        File dbFile = dbDir.toPath().resolve(fileString).normalize().toFile();
        if (list[numOfDir][numOfFile].isEmpty()) {
            dbFile.delete();
            if (dbDir.list().length == 0) {
                dbDir.delete();
            }
            return;
        }
        RandomAccessFile db;
        try {
            db = new RandomAccessFile(dbFile, "rw");
            try {
                db.setLength(0);
                Iterator<Map.Entry<String, String>> it;
                it = list[numOfDir][numOfFile].entrySet().iterator();
                long[] pointers = new long[list[numOfDir][numOfFile].size()];
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
                it = list[numOfDir][numOfFile].entrySet().iterator();
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
        } catch (Exception e) {
            throw new IllegalArgumentException();
        } 

    }
    
    public int countChanges(boolean isWrite) {
        int result = 0;
        for (Integer file : update.keySet()) {
            numberOfFile = file % 16;
            numberOfDir = (file - numberOfFile) / 16;
            if (isWrite) {
                writeIntoFile(numberOfDir, numberOfFile);
            }
            for (Map.Entry entry : list[numberOfDir][numberOfFile].entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (lastList[numberOfDir][numberOfFile].containsKey(key)) {
                    if (!lastList[numberOfDir][numberOfFile].get(key).equals(value)) {
                        ++result;
                    } 
                } else {
                    ++result;
                }
            }
            for (Map.Entry entry : lastList[numberOfDir][numberOfFile].entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (list[numberOfDir][numberOfFile].containsKey(key)) {
                    if (!list[numberOfDir][numberOfFile].get(key).equals(value)) {
                        ++result;
                    }
                } else {
                    ++result;
                }
            }
        }
        return result;
    }
    
    private void assigment(HashMap<String, String>[][] first, HashMap<String, String>[][] second) {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                first[i][j].clear();
                for (Map.Entry entry : second[i][j].entrySet()) {
                    first[i][j].put((String) entry.getKey(), (String) entry.getValue());
                }
            }
        }
    }
    
    @Override
    public int commit() {
        int result = countChanges(true);
        assigment(lastList, list);
        return result;
    }

    @Override
    public int rollback() {
        int result = countChanges(false);
        assigment(list, lastList);
        return result;
    }
    
}
