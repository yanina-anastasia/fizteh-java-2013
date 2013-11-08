package ru.fizteh.fivt.students.mikhaylova_daria.db;

import java.io.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import java.util.zip.DataFormatException;
import ru.fizteh.fivt.storage.structured.*;

public class FileMap {
    private HashMap<String, Storeable> fileMapInitial = new HashMap<String, Storeable>();
    private HashMap<String, Storeable> fileMap = new HashMap<String, Storeable>();
    private File file;
    private int size = 0;
    private Short[] id;
    Boolean isLoaded = false;

    FileMap() {

    }

    FileMap(File file, Short[] id) {
        this.file = file;
        this.id = id;
    }

    public Storeable put(String key, Storeable value, TableData table) {
        if (table == null) {
            throw new IllegalArgumentException("Table is null");
        }
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (key.contains(" ") || key.contains("\n") || key.contains("\t")) {
            throw new IllegalArgumentException("Bad char in key");
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException("key is empty");
        }
        if (!isLoaded) {
            try {
                readerFile(table);
            } catch (DataFormatException e) {
                throw new IllegalArgumentException("Bad data", e);
            } catch (Exception e) {
                throw new RuntimeException("Reading error", e);
            }

        }
        return fileMap.put(key, value);
    }

    public Storeable get(String key, TableData table) throws IllegalArgumentException {
        if (table == null) {
            throw new IllegalArgumentException("Table is null");
        }
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (key.contains(" ") || key.contains("\n") || key.contains("\t")) {
            throw new IllegalArgumentException("Bad char in key");
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException("key is empty");
        }
        if (!isLoaded) {
            try {
                readerFile(table);
            } catch (DataFormatException e) {
                throw new IllegalArgumentException("Bad data", e);
            } catch (Exception e) {
                throw new RuntimeException("Reading error", e);
            }
        }
        return fileMap.get(key);
    }

    public Storeable remove(String key, TableData table) throws IllegalArgumentException {
        if (table == null) {
            throw new IllegalArgumentException("Table is null");
        }
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (key.contains(" ") || key.contains("\n") || key.contains("\t")) {
            throw new IllegalArgumentException("Bad char in key");
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException("key is empty");
        }
        if (!isLoaded) {
            try {
                readerFile(table);
            } catch (DataFormatException e) {
                throw new IllegalArgumentException("Bad data", e);
            } catch (Exception e) {
                throw new RuntimeException("Reading error", e);
            }
        }
        return fileMap.remove(key);
    }

    private void writerFile(TableData table) throws Exception {
        if (table == null) {
            throw new IllegalArgumentException("Table is null");
        }
        RandomAccessFile fileDataBase = null;
        String stringValue;
        Exception e = new Exception("Writing error");
        try {
            fileDataBase = new RandomAccessFile(file, "rw");
            fileDataBase.setLength(0);
            HashMap<String, Long> offsets = new HashMap<String, Long>();
            long currentOffsetOfValue;
            long offset = fileDataBase.getFilePointer();
            for (String key: fileMap.keySet()) {
                fileDataBase.write(key.getBytes("UTF8"));
                fileDataBase.write("\0".getBytes());
                offset = fileDataBase.getFilePointer();
                offsets.put(key, offset);
                fileDataBase.seek(fileDataBase.getFilePointer() + 4);
                currentOffsetOfValue = fileDataBase.getFilePointer();
            }

            long currentPosition = 0;
            for (String key: fileMap.keySet()) {
                stringValue = table.manager.serialize(table, fileMap.get(key));
                fileDataBase.write(stringValue.getBytes("UTF8")); // выписали значение
                currentPosition  = fileDataBase.getFilePointer();
                currentOffsetOfValue = currentPosition - stringValue.getBytes("UTF8").length;
                fileDataBase.seek(offsets.get(key));
                Integer lastOffsetInt = new Long(currentOffsetOfValue).intValue();
                fileDataBase.writeInt(lastOffsetInt);
                fileDataBase.seek(currentPosition);
            }
        } catch (Exception exp) {
             e = exp;
        } finally {
            try {
                if (fileDataBase != null) {
                    fileDataBase.close();
                }
            } catch (Throwable th) {
                e.addSuppressed(th);
            }
        }
        if (file.length() == 0) {
            deleteEmptyFile();
        }
        size = fileMap.size();
        fileMapInitial.clear();
        for (String key: fileMap.keySet()) {
            fileMapInitial.put(key, fileMap.get(key));
        }
    }

    private boolean deleteEmptyFile() {
        isLoaded = false;
        fileMap.clear();
        return file.delete();
    }

    void setAside() {
        if (isLoaded) {
            fileMap.clear();
            fileMapInitial.clear();
            isLoaded = false;
        }
    }

    void readerFile(TableData table) throws IOException, DataFormatException, ParseException {
        if (table == null) {
            throw new IllegalArgumentException("Table is null");
        }
        Exception e = new Exception("Reading error");
        Storeable storeableValue;
        RandomAccessFile dataBase = null;
        String key1;
        try {
            dataBase = new RandomAccessFile(file, "r");
            HashMap<Integer, String> offsetAndKeyMap = new HashMap<Integer, String>();
            HashMap<String, Integer> keyAndValueLength = new HashMap<String, Integer>();
            String key = readKey(dataBase);
            byte b = key.getBytes()[0];
            if (b < 0) {
                b *= (-1);
            }
            if (id[0] != b % 16 && id[1] != b / 16 % 16) {
                throw new DataFormatException("Illegal key in file1 " + file.toPath().toString());
            }
            if (keyAndValueLength.containsKey(key)) {
                throw new DataFormatException("Illegal key in file2 " + file.toPath().toString());
            }
            Integer offset = 0;
            try {
                offset = dataBase.readInt();
            } catch (EOFException e1) {
                throw new DataFormatException(file.getName());
            }
            offsetAndKeyMap.put(offset, key);
            final int firstOffset = offset;
            try {
                int lastOffset = offset;
                String lastKey;
                while (dataBase.getFilePointer() < firstOffset) {
                    lastKey = key;
                    key = readKey(dataBase);
                    lastOffset = offset;
                    offset = dataBase.readInt();
                    offsetAndKeyMap.put(offset, key);
                    keyAndValueLength.put(lastKey, offset - lastOffset);
                    if (keyAndValueLength.containsKey(key)) {
                        throw new DataFormatException(file.getName() + ": " + key + ": The key is already contained");
                    }
                }
                keyAndValueLength.put(key, (int) dataBase.length() - offset);
            } catch (EOFException e1) {
                throw new DataFormatException(file.getName());
            }
            int lengthOfValue = 0;
            while (dataBase.getFilePointer() < dataBase.length()) {
                int currentOffset = (int) dataBase.getFilePointer();
                if (!offsetAndKeyMap.containsKey(currentOffset)) {
                    //throw new DataFormatException("Illegal key in file3 " + file.toPath().toString());
                } else {
                    key = offsetAndKeyMap.get(currentOffset);
                    lengthOfValue = keyAndValueLength.get(key);
                }
                byte[] valueInBytes = new byte[lengthOfValue];
                for (int i = 0; i < lengthOfValue; ++i) {
                    valueInBytes[i] = dataBase.readByte();
                }
                String value = new String(valueInBytes, "UTF8");
                storeableValue = table.manager.deserialize(table, value);
                fileMap.put(key, storeableValue);
            }
        } catch (FileNotFoundException e1) {
            e = e1;
            return;
        } catch (EOFException e2) {
            e = e2;
            throw new DataFormatException(file.getName());
        } finally {
            if (dataBase != null) {
                try {
                    dataBase.close();
                } catch (Throwable th) {
                    e.addSuppressed(th);
                }
            }
        }
        fileMapInitial.clear();
        for (String key: fileMap.keySet()) {
            fileMapInitial.put(key, fileMap.get(key));
        }
        size = fileMap.size();
        isLoaded = true;
    }


    private String readKey(RandomAccessFile dateBase) throws IOException, DataFormatException {
        Vector<Byte> keyBuilder = new Vector<Byte>();
        try {
            byte buf = dateBase.readByte();
            while (buf != "\0".getBytes("UTF8")[0]) {
                keyBuilder.add(buf);
                buf = dateBase.readByte();
            }
        } catch (EOFException e) {
            throw new DataFormatException(file.getName());
        }
        String key = null;
        try {
            byte[] keyInBytes = new byte[keyBuilder.size()];
            for (int i = 0; i < keyBuilder.size(); ++i) {
                keyInBytes[i] = keyBuilder.elementAt(i);
            }
            key = new String(keyInBytes, "UTF8");
        } catch (Exception e) {
            throw  new IOException(file.getName(), e);
        }
        return key;
    }

    int numberOfChangesCounter(TableData table) {
        int numberOfChanges = 0;
        Set<String> newKeys = fileMap.keySet();
        Set<String> oldKeys = fileMapInitial.keySet();
        for (String key: newKeys) {
            if (oldKeys.contains(key)) {
                String val1 = table.manager.serialize(table, fileMap.get(key));
                String val2 = table.manager.serialize(table, fileMapInitial.get(key));
                if (!val1.equals(val2)) {
                    ++numberOfChanges;
                }
            } else {
                ++numberOfChanges;
            }
        }
        for (String key: oldKeys) {
            if (!newKeys.contains(key)) {
                ++numberOfChanges;
            }
        }
        return numberOfChanges;
    }


    void commit(TableData table) {
        if (table == null) {
            throw new IllegalArgumentException("Table is null");
        }
        int numberOfChanges = numberOfChangesCounter(table);
        if (numberOfChanges != 0) {
            try {
                writerFile(table);
            } catch (Exception e) {
                throw new IllegalArgumentException("Writing error", e);
            }
        }
    }

    int rollback(TableData table) {
        int numberOfChanges = numberOfChangesCounter(table);
        fileMap.clear();
        for (String key : fileMapInitial.keySet()) {
            fileMap.put(key, fileMapInitial.get(key));
        }
        return numberOfChanges;
    }

    int size(TableData table) {
        if (table == null) {
            throw new IllegalArgumentException("Table is null");
        }
        if (!isLoaded) {
            try {
                readerFile(table);
            } catch (DataFormatException e) {
                throw new IllegalArgumentException("Bad dates", e);
            } catch (Exception e) {
                throw new IllegalArgumentException("Reading error", e);
            }
        }
        return fileMap.size();
    }

}
