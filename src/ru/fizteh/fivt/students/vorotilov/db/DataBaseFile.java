package ru.fizteh.fivt.students.vorotilov.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DataBaseFile {

    private RandomAccessFile dbFile;
    private HashMap<String, String> dbMap;

    public DataBaseFile(File dbFilePath) throws IOException, DataBaseOpenFailed {
        try {
            if (!dbFilePath.exists()) {
                if (!dbFilePath.createNewFile()) {
                    throw new CantCreateDbFile(dbFilePath.getCanonicalPath());
                }
            }
            dbFile = new RandomAccessFile(dbFilePath, "rw");
            dbMap = new HashMap<>();
            while (dbFile.getFilePointer() < dbFile.length() - 1) {
                int keyLength = dbFile.readInt();
                int valueLength = dbFile.readInt();
                if (keyLength <= 0 || valueLength <= 0) {
                    throw new DataBaseFileDamaged();
                }
                if ((long) keyLength + (long) valueLength > dbFile.length() - dbFile.getFilePointer()) {
                    throw new DataBaseFileDamaged();
                }
                byte[] key = new byte[keyLength];
                byte[] value = new byte[valueLength];
                dbFile.read(key);
                dbFile.read(value);
                dbMap.put(new String(key, Charset.forName("UTF-8")), new String(value, Charset.forName("UTF-8")));
            }
        } catch (FileNotFoundException e) {
            System.out.println("can't open data base file: '" + dbFilePath.getCanonicalPath() + "' file not found");
            throw new DataBaseOpenFailed();
        } catch (CantCreateDbFile e) {
            System.out.println("can't create new db file: '" + e.getProblematicFile() + "'");
            throw new DataBaseOpenFailed();
        } catch (IOException | DataBaseFileDamaged e) {
            System.out.println("can't read db file, it's damaged, exit without saving");
            System.exit(1);
        }
    }

    public void put(String newKey, String newValue) throws IOException {
        String currentValue = dbMap.get(newKey);
        if (currentValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(currentValue);
        }
        dbMap.put(newKey, newValue);
    }

    public String get(String newKey) throws IOException {
        String currentValue = dbMap.get(newKey);
        if (currentValue == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(currentValue);
        }
        return currentValue;
    }

    public String remove(String newKey) throws IOException {
        String currentValue = dbMap.remove(newKey);
        if (currentValue == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
        return currentValue;
    }

    public void save() {
        try {
            Set<Map.Entry<String, String>> dbSet = dbMap.entrySet();
            Iterator<Map.Entry<String, String>> i = dbSet.iterator();
            dbFile.seek(0);
            while (i.hasNext()) {
                Map.Entry<String, String> tempEntry = i.next();
                dbFile.writeInt(tempEntry.getKey().length());
                dbFile.writeInt(tempEntry.getValue().length());
                dbFile.writeUTF(tempEntry.getKey());
                dbFile.seek(dbFile.getFilePointer() - 1);
                dbFile.writeUTF(tempEntry.getValue());
                dbFile.seek(dbFile.getFilePointer() - 1);
            }
            dbFile.setLength(dbFile.getFilePointer());
        } catch (IOException e) {
            System.out.println("can't save data base file");
            System.exit(1);
        }
    }

    public void close() {
        save();
        try {
            dbFile.close();
        } catch (IOException e) {
            System.out.println("can't close data base file");
            System.exit(1);
        }
    }
}
