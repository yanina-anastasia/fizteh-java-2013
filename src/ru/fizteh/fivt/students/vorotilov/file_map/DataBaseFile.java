package ru.fizteh.fivt.students.vorotilov.file_map;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class DataBaseFile {

    private RandomAccessFile dbFile;
    private HashMap<byte[], byte[]> dbMap;

    public DataBaseFile(File dbFilePath) throws IOException {
        try {
            if (!dbFilePath.exists()) {
                if (!dbFilePath.createNewFile()) {
                    throw new CantCreateDbFile(dbFilePath.getCanonicalPath());
                }
            }
            dbFile = new RandomAccessFile(dbFilePath, "rw");
            dbMap = new HashMap<>();
            long currentFilePointer;
            while (dbFile.getFilePointer() < dbFile.length()) {
                int keyLength = dbFile.readInt();
                int valueLength = dbFile.readInt();
                byte[] key = new byte[keyLength];
                byte[] value = new byte[valueLength];
                dbFile.read(key);
                dbFile.read(value);
                dbMap.put(key, value);
            }
        } catch (FileNotFoundException e) {
            System.out.println("can't open data base file: '" + dbFilePath.getCanonicalPath() + "' file not found");
        } catch (CantCreateDbFile e) {
            System.out.println("can't create new db file: '" + e.getProblematicFile() + "'");
        }
    }

    public void put(byte[] newKey, byte[] newValue) throws IOException {
        byte[] currentValue = dbMap.get(newKey);
        if (currentValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(currentValue);
        }
        dbMap.put(newKey, newValue);
    }

    public byte[] get(byte[] newKey) throws IOException {
        byte[] currentValue = dbMap.get(newKey);
        if (currentValue == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(new String(currentValue));
        }
        return currentValue;
    }

    public byte[] remove(byte[] newKey) throws IOException {
        byte[] currentValue = dbMap.remove(newKey);
        if (currentValue == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
        return currentValue;
    }

    public void save() throws IOException {

        dbFile.close();
    }

    @Override
    protected void finalize() throws Throwable {
        save();
    }

}

