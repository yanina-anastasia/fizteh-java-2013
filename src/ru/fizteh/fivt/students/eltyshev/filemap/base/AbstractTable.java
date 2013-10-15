package ru.fizteh.fivt.students.eltyshev.filemap.base;

import ru.fizteh.fivt.storage.strings.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractTable implements Table {

    protected static final Charset CHARSET = StandardCharsets.UTF_8;

    private class Reader {
        RandomAccessFile file;
        int valuesOffset = -1;

        public Reader(String filePath) throws IOException {
            try {
                file = new RandomAccessFile(filePath, "r");
            } catch (FileNotFoundException e) {
                file = null;
                valuesOffset = 0;
                return;
            }
            // initializing beginning of value section
            skipKey();
            valuesOffset = readOffset();
            file.seek(0);
        }

        public String readKey() throws IOException {
            if (file.getFilePointer() >= valuesOffset) {
                return null;
            }
            ArrayList<Byte> bytes = new ArrayList<Byte>();
            byte b = file.readByte();
            while (b != 0) {
                bytes.add(b);
                b = file.readByte();
            }
            byte[] array = FileMapUtils.toByteArray(bytes);
            return new String(array, CHARSET);
        }

        public String readValue() throws IOException {
            int offset = readOffset();
            int nextOffset = readNextOffset();
            long currentOffset = file.getFilePointer();
            file.seek(offset);
            int valueLength = nextOffset - offset;
            byte[] bytes = new byte[valueLength];
            file.read(bytes, 0, valueLength);
            file.seek(currentOffset);
            return new String(bytes, CHARSET);
        }

        public boolean endOfFile() {
            if (file == null) {
                return true;
            }

            boolean result = true;
            try {
                result = (file.getFilePointer() == valuesOffset);
            } finally {
                return result;
            }
        }

        public void close() {
            try {
                file.close();
            } catch (IOException e) {
                // so sad
            }
        }

        private int readNextOffset() throws IOException {
            long currentOffset = file.getFilePointer();
            int nextOffset;
            if (readKey() == null) // if we have not other keys
            {
                nextOffset = (int) file.length();
            } else {
                nextOffset = readOffset();
            }
            file.seek(currentOffset);
            return nextOffset;
        }

        private void skipKey() throws IOException {
            byte b;
            do {
                b = file.readByte();
            } while (b != 0);
        }

        private int readOffset() throws IOException {
            return file.readInt();
        }
    }

    private class Writer {
        private RandomAccessFile file;

        public Writer(String filePath) throws IOException {
            try {
                file = new RandomAccessFile(filePath, "rw");
            } catch (FileNotFoundException e) {
                throw new IOException(String.format("error while creating file: '%s'", filePath));
            }
            file.setLength(0);
        }

        public void writeKey(String key) throws IOException {
            byte[] bytes = key.getBytes(CHARSET);
            file.write(bytes);
            file.writeByte(0);
        }

        public void writeOffset(int offset) throws IOException {
            file.writeInt(offset);
        }

        public void writeValue(String value) throws IOException {
            byte[] bytes = value.getBytes(CHARSET);
            file.write(bytes);
        }

        public void close() {
            try {
                file.close();
            } catch (IOException e) {
                // so sad
            }
        }
    }

    // Data
    protected HashMap<String, String> oldData;
    protected HashMap<String, String> modifiedData;
    protected HashSet<String> deletedKeys;

    private String tableName;
    private int size;
    private String directory;
    private int uncommittedChangesCount;

    // Strategy
    protected abstract void load() throws IOException;

    protected abstract void save() throws IOException;

    // Constructor
    public AbstractTable(String directory, String tableName) {
        this.directory = directory;
        this.tableName = tableName;
        oldData = new HashMap<String, String>();
        modifiedData = new HashMap<String, String>();
        deletedKeys = new HashSet<String>();
        uncommittedChangesCount = 0;
        try {
            load();
        } catch (IOException e) {
            System.err.println("error loading table: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("error loading table: " + e.getMessage());
        }
    }

    public int getUncommitedChangesCount() {
        return uncommittedChangesCount;
    }

    // Table implementation
    public String getName() {
        return tableName;
    }

    public String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null!");
        }
        if (modifiedData.containsKey(key)) {
            return modifiedData.get(key);
        }
        if (deletedKeys.contains(key)) {
            return null;
        }

        return oldData.get(key);
    }

    public String put(String key, String value) throws IllegalArgumentException {
        if (key == null || value == null) {
            String message = key == null ? "key " : "value ";
            throw new IllegalArgumentException(message + "cannot be null");
        }
        String oldValue = getOldValueFor(key);
        modifiedData.put(key, value);
        if (oldValue == null) {
            size += 1;
        }
        uncommittedChangesCount += 1;
        return oldValue;
    }

    public String remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        String oldValue = getOldValueFor(key);
        if (modifiedData.containsKey(key)) {
            modifiedData.remove(key);
            if (oldData.containsKey(key)) {
                deletedKeys.add(key);
            }
        } else {
            deletedKeys.add(key);
        }
        if (oldValue != null) {
            size -= 1;
        }
        uncommittedChangesCount += 1;
        return oldValue;
    }

    public int size() {
        return size;
    }

    public int commit() {
        int recordsCommited = Math.abs(oldData.size() - size);
        for (final String keyToDelete : deletedKeys) {
            oldData.remove(keyToDelete);
        }
        for (final String keyToAdd : modifiedData.keySet()) {
            oldData.put(keyToAdd, modifiedData.get(keyToAdd));
        }
        deletedKeys.clear();
        modifiedData.clear();
        size = oldData.size();
        try {
            save();
        } catch (IOException e) {
            System.err.println("commit: " + e.getMessage());
            return 0;
        }
        uncommittedChangesCount = 0;

        return recordsCommited;
    }

    public int rollback() {
        int recordsDeleted = Math.abs(oldData.size() - size);
        deletedKeys.clear();
        modifiedData.clear();
        size = oldData.size();

        uncommittedChangesCount = 0;

        return recordsDeleted;
    }

    // internal methods
    protected String getDirectory() {
        return directory;
    }

    protected void loadFromFile(String fileName) throws IOException {
        if (!FileMapUtils.checkFileExists(fileName)) {
            return;
        }
        Reader reader = new Reader(fileName);
        while (!reader.endOfFile()) {
            String key = reader.readKey();
            String value = reader.readValue();
            oldData.put(key, value);
        }
        reader.close();
    }

    protected void saveToFile(Set<String> keys, String fileName) throws IOException {
        Writer writer = new Writer(fileName);
        int offset = getKeysLength(keys);

        for (final String key : keys) {
            writer.writeKey(key);
            writer.writeOffset(offset);
            offset += FileMapUtils.getByteCount(oldData.get(key), CHARSET);
        }
        for (final String key : keys) {
            writer.writeValue(oldData.get(key));
        }
        writer.close();
    }

    private int getKeysLength(Set<String> keys) {
        int keysLength = 0;
        for (final String key : keys) {
            int keyLength = FileMapUtils.getByteCount(key, CHARSET);
            keysLength += keyLength + 5;
        }
        return keysLength;
    }

    private String getOldValueFor(String key) {
        String oldValue = null;
        oldValue = modifiedData.get(key);
        // Если новое значение не было изменено\добавлено и не было удалено
        if (oldValue == null && !deletedKeys.contains(key)) {
            oldValue = oldData.get(key);
        }
        return oldValue;
    }
}
