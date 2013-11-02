package ru.fizteh.fivt.students.vorotilov.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

public class TableFile implements AutoCloseable {

    private RandomAccessFile tableFile;
    private File tableFilePath;
    private boolean writeMode;

    public class Entry {
        private String key;
        private String value;

        public Entry(String key, String value) {
            if (key == null) {
                throw new IllegalArgumentException("Key is null");
            }
            if (value == null) {
                throw new IllegalArgumentException("Value is null");
            }
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

    }

    TableFile(File tableFilePath) {
        if (tableFilePath == null) {
            throw new IllegalArgumentException("Path to table file is null");
        } else if (!tableFilePath.exists()) {
            try {
                if (!tableFilePath.createNewFile()) {
                    throw new IllegalStateException("New empty file was not created");
                }
            } catch (IOException e) {
                throw new IllegalStateException("Can't create empty table file");
            }
        }
        try {
            this.tableFilePath = tableFilePath;
            tableFile = new RandomAccessFile(tableFilePath, "rw");
            setReadMode();
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("File must exist but not found");
        }
    }

    public Entry readEntry() {
        if (!isReadMode()) {
            throw new IllegalStateException("Attempt to read in write mode");
        }
        Entry tempEntry;
        try {
            if (tableFile.length() == 0) {
                throw new IllegalStateException("Table file is empty");
            }
            int keyLength = tableFile.readInt();
            int valueLength = tableFile.readInt();
            if (keyLength <= 0 || valueLength <= 0) {
                throw new IllegalStateException("Table file is damaged");
            }
            if ((long) keyLength + (long) valueLength > tableFile.length() - tableFile.getFilePointer()) {
                throw new IllegalStateException("Table file is damaged");
            }
            byte[] key = new byte[keyLength];
            byte[] value = new byte[valueLength];
            int readedBytes = 0;
            while (readedBytes != keyLength) {
                int nowReaded = tableFile.read(key, readedBytes, keyLength - readedBytes);
                if (nowReaded == -1) {
                    throw new IllegalStateException("Key was not completely readed, but EOF is reached");
                }
                readedBytes += nowReaded;
            }
            readedBytes = 0;
            while (readedBytes != valueLength) {
                int nowReaded = tableFile.read(value, readedBytes, valueLength - readedBytes);
                if (nowReaded == -1) {
                    throw new IllegalStateException("Value was not completely readed, but EOF is reached");
                }
                readedBytes += nowReaded;
            }
            tempEntry = new Entry(new String(key, Charset.forName("UTF-8")),
                    new String(value, Charset.forName("UTF-8")));
        } catch (IOException e) {
            throw new IllegalStateException("Can't make file operations");
        }
        return tempEntry;
    }

    public boolean hasNext() {
        if (!isReadMode()) {
            throw new IllegalStateException("Attempt to check next in write mode");
        }

        long currentFilePointer;
        long length;
        try {
            currentFilePointer = tableFile.getFilePointer();
            length = tableFile.length();
        } catch (IOException e) {
            throw new IllegalStateException("Can't resolve hasNext");
        }
        return (currentFilePointer < length);
    }

    public void writeEntry(Entry newEntry) {
        if (!isWriteMode()) {
            throw new IllegalStateException("Attempt to write in read mode");
        }
        try {
            tableFile.writeInt(newEntry.getKey().getBytes("UTF-8").length);
            tableFile.writeInt(newEntry.getValue().getBytes("UTF-8").length);
            tableFile.write(newEntry.getKey().getBytes("UTF-8"));
            tableFile.write(newEntry.getValue().getBytes("UTF-8"));
            tableFile.setLength(tableFile.getFilePointer());
        } catch (IOException e) {
            throw new IllegalStateException("Can't write entry in file");
        }
    }

    public void writeEntry(String key, String value) {
        writeEntry(new Entry(key, value));
    }

    public boolean isWriteMode() {
        return writeMode;
    }

    public boolean isReadMode() {
        return !writeMode;
    }

    public void setWriteMode() {
        if (!isWriteMode()) {
            try {
                tableFile.setLength(0);
                tableFile.seek(0);
            } catch (IOException e) {
                throw new IllegalStateException("Can't trim file to 0 length");
            }
            writeMode = true;
        }
    }

    public void setReadMode() {
        if (!isReadMode()) {
            try {
                tableFile.seek(0);
            } catch (IOException e) {
                throw new IllegalStateException("Can't trim file to 0 length");
            }
            writeMode = false;
        }
    }

    @Override
    public void close() throws Exception {
        tableFile.close();
        if (tableFilePath.length() == 0) {
            if (!tableFilePath.delete()) {
                throw new IllegalStateException("Can't delete empty file");
            }
        }
    }

}
