package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.Vector;

class FileDatabase implements AutoCloseable {
    private static final int OFFSET_BYTES = 4;

    Path dbFilePath;
    RandomAccessFile dbFile;
    Hashtable<String, String> database = new Hashtable<String, String>();

    public FileDatabase(Path dbFilePath) {
        try {
            this.dbFilePath = dbFilePath;
            Files.createDirectories(dbFilePath.getParent());
            
            dbFile = new RandomAccessFile(dbFilePath.toFile(), "rw");
            getDataFromFile();
            
        } catch (Exception e) {
            
            System.out.println("Error while opening database file: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unkonown error"));
            try {
                if (dbFile != null) {
                    dbFile.close();
                }
            } catch (IOException e1) {
                System.out
                        .println("Error while closing database: "
                                + ((e1.getMessage() != null) ? e1.getMessage()
                                        : "unkonown error"));
            }
            System.exit(1);
            
        }
    }

    public String put(String key, String value) throws Exception {
    
        String oldValue = database.put(key, value);

        saveChanges();
        
        return oldValue;
        /*if (oldValue != null) {
            System.out.println("overwrite");
            System.out.println(oldValue);
        } else {
            System.out.println("new");
        }*/
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
        /*if (value != null) {
            saveChanges();
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }*/
        
    }
    
    public int getSize() {
        return database.size();
    }

    public void getDataFromFile() throws IOException {
        String key;
        String value;
        Vector<Byte> tempKey = new Vector<Byte>();
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
                    tempArray[i] = tempKey.elementAt(i).byteValue();
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
