package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class FileDatabase implements AutoCloseable {
    private static final int OFFSET_BYTES = 4;

    Path dbFilePath;
    RandomAccessFile dbFile;
    Map<String, String> database = new HashMap<String, String>();

    public FileDatabase(Path dbFilePath) throws Exception {
        try {
            this.dbFilePath = dbFilePath;
            Files.createDirectories(dbFilePath.getParent());
            
            dbFile = new RandomAccessFile(dbFilePath.toFile(), "rw");
            getDataFromFile();
            
        } catch (Exception e) {
            Exception exception = new Exception("Error while opening database file: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
            try {
                if (dbFile != null) {
                    dbFile.close();
                }
            } catch (Exception e1) {
                exception.addSuppressed(new Exception("Error while closing database: "
                                + ((e1.getMessage() != null) ? e1.getMessage()
                                        : "unknown error"), e1));
            }
            
            throw exception;
        }
    }

    public String put(String key, String value) throws Exception {
    
        String oldValue = database.put(key, value);

        saveChanges();
        
        return oldValue;
    }
    
    public String get(String key) throws Exception {
        return database.get(key);
    }
    
    public String remove(String key) throws Exception {
        
        String value = database.remove(key);

        if (value != null) {
            saveChanges();
        }
        return value;
        
    }
    
    public int getSize() {
        return database.size();
    }

    public void getDataFromFile() throws IOException {
        String key;
        String value;
        ArrayList<Byte> tempKey = new ArrayList<Byte>();
        int tempOffset1;
        int tempOffset2;
        long currentPosition;
        byte tempByte;
        byte[] tempArray;

        dbFile.seek(0);
        while (dbFile.getFilePointer() != dbFile.length()) {
            tempByte = dbFile.readByte();
            if (tempByte != '\0') {
                tempKey.add(tempByte);
            } else {
                tempOffset1 = dbFile.readInt();
                currentPosition = dbFile.getFilePointer();
                while (dbFile.readByte() != '\0'
                        && dbFile.getFilePointer() != dbFile.length());
                if (dbFile.getFilePointer() == dbFile.length()) {
                    tempOffset2 = (int) dbFile.length();
                } else {
                    tempOffset2 = dbFile.readInt();
                }
                dbFile.seek(tempOffset1);
                tempArray = new byte[tempOffset2 - tempOffset1];
                dbFile.readFully(tempArray);
                value = new String(tempArray, "UTF-8");
                tempArray = new byte[tempKey.size()];
                for (int i = 0; i < tempKey.size(); ++i) {
                    tempArray[i] = tempKey.get(i).byteValue();
                }
                key = new String(tempArray, "UTF-8");
                database.put(key, value);
                tempKey.clear();
                dbFile.seek(currentPosition);
            }
        }
    }

    void saveChanges() throws IOException {
        int currentOffset = 0;
        long returnPosition;
        String value;

        dbFile.setLength(0);

        for (String key : database.keySet()) {
            currentOffset += key.getBytes("UTF-8").length + OFFSET_BYTES + 1;
        }

        for (String key : database.keySet()) {
            dbFile.write(key.getBytes("UTF-8"));
            dbFile.writeByte(0);
            dbFile.writeInt(currentOffset);
            value = database.get(key);
            returnPosition = dbFile.getFilePointer();
            dbFile.seek(currentOffset);
            dbFile.write(value.getBytes("UTF-8"));

            currentOffset += value.getBytes("UTF-8").length;
            dbFile.seek(returnPosition);
        }
    }
    
    @Override
    public void close() throws Exception {
        if (dbFile.length() == 0) {
            dbFile.close();
            Files.delete(dbFilePath);
            if (dbFilePath.getParent().toFile().list().length == 0) {
                Files.delete(dbFilePath.getParent());
            }
        }
        dbFile.close();
    }
}
