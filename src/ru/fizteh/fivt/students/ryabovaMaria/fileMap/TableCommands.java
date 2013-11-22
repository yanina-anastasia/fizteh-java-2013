package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public class TableCommands implements Table {
    private final ArrayList<Class<?>> types;
    private final TableProvider tableProvider;
    private final File tableDir;
    //private ThreadLocal<HashMap<String, String>[][]> diff = new ThreadLocal();
    //private ThreadLocal<HashMap<String, String>[][]> list = new ThreadLocal();
    private HashMap<String, String>[][] lastList;
    //private ThreadLocal<HashMap<Integer, String>> update = new ThreadLocal();
    //private ThreadLocal<Integer> hashCode = new ThreadLocal();
    //private ThreadLocal<Integer> numberOfDir = new ThreadLocal();
    //private ThreadLocal<Integer> numberOfFile = new ThreadLocal();
    private ReadWriteLock lock;
    private Lock readLock;
    private Lock writeLock;
    
    private ThreadLocal<Integer> hashCode = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return new Integer(0);
        }
    };
    
    private ThreadLocal<Integer> numberOfDir = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return new Integer(0);
        }
    };
    
    private ThreadLocal<Integer> numberOfFile = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return new Integer(0);
        }
    };
    
    private ThreadLocal<HashMap<String, String>[][]> diff = new ThreadLocal<HashMap<String, String>[][]>() {
        @Override
        protected HashMap<String, String>[][] initialValue() {
            HashMap<String, String> diffObject[][] = new HashMap[16][16];
            lastList = new HashMap[16][16];
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    diffObject[i][j] = new HashMap<String, String>();
                }
            }
            return diffObject;
        }
    };
    
    private ThreadLocal<HashMap<String, String>[][]> list = new ThreadLocal<HashMap<String, String>[][]>() {
        @Override
        protected HashMap<String, String>[][] initialValue() {
            HashMap<String, String> listObject[][] = new HashMap[16][16];
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    listObject[i][j] = new HashMap<String, String>();
                }
            }
            return listObject;
        }
    };
    
    private ThreadLocal<HashMap<Integer, String>> update = new ThreadLocal<HashMap<Integer, String>>() {
        @Override
        protected HashMap<Integer, String> initialValue() {
            return new HashMap<Integer, String>();
        }
    };
    
    TableCommands(File directory, List<Class<?>> types, TableProvider tableProvider) throws IOException {
        lock = new ReentrantReadWriteLock(true);
        readLock = lock.readLock();
        writeLock = lock.writeLock();
        this.tableProvider = tableProvider;
        this.types = new ArrayList(types);
        lastList = new HashMap[16][16];
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                lastList[i][j] = new HashMap<String, String>();
            }
        }
        tableDir = directory;
        writeLock.lock();
        try {
            isCorrectTable();
        } finally {
            writeLock.unlock();
        }
    }
    
    private void isCorrectTable() throws IOException {
        String[] listOfDirs = tableDir.list();
        int countOk = 0;
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
                ++countOk;
                isCorrectDir(value, listOfDirs[i]);
            } else {
                if (!(listOfDirs[i].equals("signature.tsv") && new File(tableDir, listOfDirs[i]).isFile())) {
                    throw new IllegalArgumentException("Incorrect table");
                }
            }
        }
        if (countOk == listOfDirs.length) {
            throw new IllegalArgumentException("no signature.tsv");
        }
    }
    
    private void isCorrectDir(int numOfDir, String name) throws IOException {
        File curDir = tableDir.toPath().resolve(name).normalize().toFile();
        String[] listOfFiles = curDir.list();
        if (listOfFiles.length == 0) {
            throw new IllegalArgumentException("empty dir"); 
        }
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
    
    private void isCorrectFile(int numOfDir, int numOfFile, File current, String name) throws IOException {
        File dbFile = current.toPath().resolve(name).normalize().toFile();
        if (!dbFile.exists()) {
            throw new IllegalArgumentException("Incorrect table");
        }
        if (!dbFile.isFile()) {
            throw new IllegalArgumentException("Incorrect table");
        }
        try (RandomAccessFile db = new RandomAccessFile(dbFile, "rw")) {
            long curPointer = 0;
            long lastPointer = 0;
            long length = db.length();
            if (length == 0) {
                throw new IllegalArgumentException("empty file");
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
                        if (lastList[numOfDir][numOfFile].containsKey(lastKey)) {
                            System.err.println(lastKey + " meets twice in db.dat");
                            System.exit(1);
                        }
                        lastList[numOfDir][numOfFile].put(lastKey, lastValue);
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
            if (lastList[numOfDir][numOfFile].containsKey(lastKey)) {
                throw new IllegalArgumentException("Incorrect file" + dbFile.toString());
            }
            lastList[numOfDir][numOfFile].put(lastKey, lastValue);
        } catch(Exception e) {
            throw new IOException("Incorrect table", e);
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
        hashCode.set(Math.abs(key.hashCode()));
        numberOfDir.set(hashCode.get() % 16);
        numberOfFile.set(hashCode.get() / 16 % 16);
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Bad key");
        }
        getUsingDatFile(key);
        String value;
        readLock.lock();
        try {
            value = lastList[numberOfDir.get()][numberOfFile.get()].get(key);
        } finally {
            readLock.unlock();
        }
        if (diff.get()[numberOfDir.get()][numberOfFile.get()].containsKey(key)) {
            value = diff.get()[numberOfDir.get()][numberOfFile.get()].get(key);
        }
        readLock.lock();
        try {
            return tableProvider.deserialize(this, value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Storeable put(String key, Storeable value) {
        if (value == null || key == null || key.isEmpty() || key.matches(".*\\s.*")) {
            throw new IllegalArgumentException("Bad args");
        }
        try {
            getUsingDatFile(key);
            update.get().put(numberOfDir.get() * 16 + numberOfFile.get(), " ");
            readLock.lock();
            String stringValue;
            try {
                stringValue = tableProvider.serialize(this, value);
            } finally {
                readLock.unlock();
            }
            Storeable answer = get(key);
            diff.get()[numberOfDir.get()][numberOfFile.get()].put(key, stringValue);
            return answer;
        } catch (Exception e) {
            throw new ColumnFormatException("incorrect args", e);
        }
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Bad args");
        }
        getUsingDatFile(key);
        update.get().put(numberOfDir.get() * 16 + numberOfFile.get(), null);
        Storeable answer = get(key);
        diff.get()[numberOfDir.get()][numberOfFile.get()].put(key, null);
        return answer;
    }
    
    private int getCountSize(int first, int second) {
        readLock.lock();
        int result = 0;
        try {
            result = lastList[first][second].size();
            for (Map.Entry entry : diff.get()[first][second].entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (value == null){
                    if (lastList[first][second].containsKey(key)) {
                        --result;
                    }
                } else {
                    if (!lastList[first][second].containsKey(key)) {
                        ++result;
                    }
                }
            }
        } finally {
            readLock.unlock();
            return result;
        }
    }

    @Override
    public int size() {
        int countSize = 0;
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                countSize += getCountSize(i, j);
            }
        }
        return countSize; 
    }
    
    private void writeIntoFile(int numOfDir, int numOfFile) throws IOException {
        String dirString = String.valueOf(numOfDir) + ".dir";
        String fileString = String.valueOf(numOfFile) + ".dat";
        File dbDir = tableDir.toPath().resolve(dirString).normalize().toFile();
        if (!dbDir.isDirectory()) {
            if (!dbDir.mkdir()) {
                throw new IOException("incorrect file");
            }
        }
        File dbFile = dbDir.toPath().resolve(fileString).normalize().toFile();
        if (list.get()[numOfDir][numOfFile].isEmpty()) {
            dbFile.delete();
            if (dbDir.list().length == 0) {
                if (!dbDir.delete()) {
                    throw new IOException("incorrect file");
                }
            }
            return;
        }
        try (RandomAccessFile db = new RandomAccessFile(dbFile, "rw")) {   
            db.setLength(0);
            Iterator<Map.Entry<String, String>> it;
            it = list.get()[numOfDir][numOfFile].entrySet().iterator();
            long[] pointers = new long[list.get()[numOfDir][numOfFile].size()];
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
            it = list.get()[numOfDir][numOfFile].entrySet().iterator();
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
            throw new IOException("incorrect file", e);
        }
    }
    
    public int countChanges(boolean isWrite) throws IOException {
        int result = 0;
        for (Integer file : update.get().keySet()) {
            numberOfFile.set(file % 16);
            numberOfDir.set((file - numberOfFile.get()) / 16);
            if (isWrite) {
                HashMap<String, String>[][] listObject = new HashMap[16][16];
                listObject[numberOfDir.get()][numberOfFile.get()] = new HashMap<String, String>();
                list.set(listObject);
                for(Map.Entry entry : lastList[numberOfDir.get()][numberOfFile.get()].entrySet()) {
                    String key = (String) entry.getKey();
                    String value = (String) entry.getValue();
                    list.get()[numberOfDir.get()][numberOfFile.get()].put(key, value);
                }
            }
            for (Map.Entry entry : diff.get()[numberOfDir.get()][numberOfFile.get()].entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (value == null) {
                    if (lastList[numberOfDir.get()][numberOfFile.get()].containsKey(key)) {
                        ++result;
                        if (isWrite) {
                            list.get()[numberOfDir.get()][numberOfFile.get()].remove(key);
                        }
                    }
                } else {
                    if (!value.equals(lastList[numberOfDir.get()][numberOfFile.get()].get(key))) {
                        ++result;
                        if (isWrite) {
                            list.get()[numberOfDir.get()][numberOfFile.get()].put(key, value);
                        }
                    }
                }
            }
            if (isWrite) {
                writeIntoFile(numberOfDir.get(), numberOfFile.get());
                if (!list.get()[numberOfDir.get()][numberOfFile.get()].isEmpty()) {
                    list.get()[numberOfDir.get()][numberOfFile.get()].clear();
                }
            }
        }
        return result;
    }
    
    @Override
    public int commit() throws IOException {
        int result = 0;
        writeLock.lock();
        try {
            result = countChanges(true);
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (!diff.get()[i][j].isEmpty()) {
                        diff.get()[i][j].clear();
                    }
                    if (!lastList[i][j].isEmpty()) {
                        lastList[i][j].clear();
                    }
                }
            }
            isCorrectTable();
        } finally {
            writeLock.unlock();
            return result;
        }
    }

    @Override
    public int rollback() {
        int result = 0;
        readLock.lock();
        try {
            result = countChanges(false);
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (!diff.get()[i][j].isEmpty()) {
                        diff.get()[i][j].clear();
                    }
                }
            }
        } catch (IOException e) {
        } finally {
            readLock.unlock();
        }
        return result;
    }

    @Override
    public int getColumnsCount() {
        readLock.lock();
        try {
            return types.size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        readLock.lock();
        try {
            if (columnIndex < 0 || columnIndex >= types.size()) {
                throw new IndexOutOfBoundsException();
            }
            return types.get(columnIndex);
        } finally {
            readLock.unlock();
        }
    }
}
