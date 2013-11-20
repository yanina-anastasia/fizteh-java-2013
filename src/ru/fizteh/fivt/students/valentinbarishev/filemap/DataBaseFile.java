package ru.fizteh.fivt.students.valentinbarishev.filemap;


import ru.fizteh.fivt.storage.structured.TableProvider;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.FileNotFoundException;

public class DataBaseFile {

    protected final String fileName;
    protected File file;
    private File dir;
    private int fileNumber;
    private int direcotryNumber;
    private DataBase table;
    private TableProvider provider;

    private Map<String, String> old;
    private Map<String, String> diff;
    private Set<String> deleted;

    public DataBaseFile(final String newFileName, final int newDirectoryNumber, final int newFileNumber,
                        DataBase newTable, TableProvider newProvider) throws IOException {
        fileName = newFileName;

        table = newTable;
        provider = newProvider;

        fileNumber = newFileNumber;
        direcotryNumber = newDirectoryNumber;

        file = new File(fileName);
        dir = new File(file.getParent());

        old = new HashMap<>();
        diff = new HashMap<>();
        deleted = new HashSet<>();

        load();
        check();
    }

    public boolean check() throws IOException {
        for (String key : old.keySet()) {
            int zeroByte = Math.abs(key.charAt(0));
            if (!((zeroByte % 16 == direcotryNumber) && ((zeroByte / 16) % 16 == fileNumber))) {
                throw new IOException("Wrong file format key[0] =  " + String.valueOf(zeroByte)
                        + " in file " + fileName);
            }
            try {
                provider.deserialize(table, key);
            } catch (ParseException e) {
                throw new IOException("Invalid file format! (parse exception error!)");
            }
        }
        return true;
    }

    private void load() {
        try {
            if (dir.exists() && dir.list().length == 0) {
                throw new IOException("Empty dir!");
            }
            if (!dir.exists() || !file.exists()) {
                return;
            }
            try (RandomAccessFile inputFile = new RandomAccessFile(fileName, "rw")) {
                while (inputFile.getFilePointer() < inputFile.length() - 1) {

                    int keyLength = inputFile.readInt();
                    int valueLength = inputFile.readInt();

                    if ((keyLength <= 0) || (valueLength <= 0)) {
                        throw new DataBaseWrongFileFormat("Wrong file format! " + file.getName());
                    }

                    byte[] key;
                    byte[] value;

                    try {
                        key = new byte[keyLength];
                        value = new byte[valueLength];
                    } catch (OutOfMemoryError e) {
                        throw new DataBaseWrongFileFormat("Some key or value are too large in " + file.getName());
                    }

                    inputFile.read(key);
                    inputFile.read(value);

                    old.put(new String(key, StandardCharsets.UTF_8), new String(value, StandardCharsets.UTF_8));
                }
            }
            if (old.size() == 0) {
                throw new IOException("Empty file!");
            }
        } catch (FileNotFoundException e) {
            throw new DataBaseException("File not found!");
        } catch (IOException e) {
            throw new DataBaseException("File load error!");
        }
    }

    public void createPath() {
        if (dir.exists()) {
            return;
        }

        if (!dir.mkdir()) {
            throw new DataBaseException("Cannot create directory!");
        }
    }

    public void deletePath() {
        if (!dir.exists()) {
            return;
        }

        if (dir.list().length != 0) {
            return;
        }

        if (!dir.delete()) {
            throw new DataBaseException("Cannot delete a directory!");
        }
    }

    public void save() {
        try {
            if (getSize() == 0) {
                if ((file.exists()) && (!file.delete())) {
                    throw new DataBaseException("Cannot delete a file!");
                }
                deletePath();
            } else {
                createPath();
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        throw new DataBaseException("Cannot create a file " + fileName);
                    }
                }
                try (RandomAccessFile outputFile = new RandomAccessFile(fileName, "rw")) {
                    for (Map.Entry<String, String> node : old.entrySet()) {
                        outputFile.writeInt(node.getKey().getBytes(StandardCharsets.UTF_8).length);
                        outputFile.writeInt(node.getValue().getBytes(StandardCharsets.UTF_8).length);
                        outputFile.write(node.getKey().getBytes(StandardCharsets.UTF_8));
                        outputFile.write(node.getValue().getBytes(StandardCharsets.UTF_8));
                    }
                    outputFile.setLength(outputFile.getFilePointer());
                }
            }
        } catch (FileNotFoundException e) {
            throw new DataBaseException("File save error!");
        } catch (IOException e) {
            throw new DataBaseException("Write to file error!");
        }
    }

    public String put(final String key, final String value) {
        String result = null;

        if (diff.containsKey(key)) {
            result = diff.get(key);
        } else {
            if (old.containsKey(key)) {
                result = old.get(key);
            }
        }

        if (deleted.contains(key)) {
            deleted.remove(key);
            result = null;
        }

        diff.put(key, value);

        return result;
    }

    public String get(final String key) {
        if (deleted.contains(key)) {
            return null;
        }

        if (diff.containsKey(key)) {
            return diff.get(key);
        }

        if (old.containsKey(key)) {
            return old.get(key);
        }

        return null;
    }

    public String remove(final String key) {
        if (deleted.contains(key)) {
            return null;
        }

        String result = null;

        if (diff.containsKey(key)) {
            result = diff.get(key);
            diff.remove(key);
            deleted.add(key);
            return result;
        }

        if (old.containsKey(key)) {
            result = old.get(key);
            deleted.add(key);
        }

        return result;
    }

    private void normalize() {
        Set<String> newDeleted = new HashSet<>();

        for (String key : old.keySet()) {
            if (old.get(key).equals(diff.get(key))) {
                diff.remove(key);
            }
            if (deleted.contains(key)) {
                newDeleted.add(key);
            }
        }

        for (String key : deleted) {
            if (diff.containsKey(key)) {
                diff.remove(key);
            }
        }

        deleted.clear();
        deleted = newDeleted;
    }

    public int getNewKeys() {
        normalize();
        return diff.size() + deleted.size();
    }

    public int getSize() {
        normalize();
        int result = diff.size() + old.size() - deleted.size();
        for (String key : diff.keySet()) {
            if (old.containsKey(key)) {
                --result;
            }
        }
        return result;
    }

    public void commit() {
        normalize();
        for (Map.Entry<String, String> node : diff.entrySet()) {
            old.put(node.getKey(), node.getValue());
        }

        for (String key : deleted) {
            old.remove(key);
        }

        diff.clear();
        deleted.clear();

        save();
    }

    public void rollback() {
        diff.clear();
        deleted.clear();
    }
}
