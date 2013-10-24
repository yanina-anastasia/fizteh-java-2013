package ru.fizteh.fivt.students.kochetovnicolai.fileMap;

import ru.fizteh.fivt.students.kochetovnicolai.shell.FileManager;
import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class BasicTable extends FileManager implements Table {
    protected File currentFile;
    protected String tableName;
    protected HashMap<String, String> changes;
    protected int recordNumber;
    protected int oldRecordNumber;

    protected BasicTable() {
    }

    public BasicTable(File tableDirectory, String name) throws IOException {
        currentPath = tableDirectory;
        tableName = name;
        if (!currentPath.exists()) {
            if (!currentPath.mkdir()) {
                throw new IOException(currentPath.getAbsolutePath() + ": couldn't create directory");
            }
        }
        currentFile = new File(currentPath.getAbsolutePath() + File.separator + "db.dat");
        if (!currentFile.exists()) {
            if (!currentFile.createNewFile()) {
                throw new IOException("couldn't create file db.dat");
            }
        }
        FileInputStream fileInputStream = new FileInputStream(currentFile);
        DataInputStream inputStream = new DataInputStream(fileInputStream);
        oldRecordNumber = 0;
        while (readNextPair(inputStream) != null) {
            oldRecordNumber++;
        }
        if (fileInputStream.read() != -1) {
            throw new IOException("invalid file");
        }
        inputStream.close();
        changes = new HashMap<>();
        rollback();
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (!changes.containsKey(key)) {
            changes.put(key, readValue(key));
        }
        return changes.get(key);
    }

    @Override
    public String put(String key, String value) throws IllegalArgumentException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        if (get(key) == null) {
            recordNumber++;
        }
        return changes.put(key, value);
    }

    @Override
    public String remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (get(key) != null) {
            recordNumber--;
        }
        return changes.put(key, null);
    }

    @Override
    public int size() {
        return recordNumber;
    }

    @Override
    public int commit() {
        int updated = changes.size();
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(currentFile))) {
            File buffer = new File(currentFile.getAbsolutePath() + '~');
            DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(buffer));
            String nextKey;
            String nextValue;
            String[] pair;
            while ((pair = readNextPair(inputStream)) != null) {
                nextKey = pair[0];
                nextValue = pair[1];
                if (changes.containsKey(nextKey)) {
                    nextValue = changes.get(nextKey);
                    changes.remove(nextKey);
                }
                if (nextValue != null) {
                    writeNextPair(outputStream, nextKey, nextValue);
                }
            }
            Set<Entry<String, String>> entries = changes.entrySet();
            for (Entry<String, String> entry : entries) {
                if (entry.getValue() != null) {
                    writeNextPair(outputStream, entry.getKey(), entry.getValue());
                }
            }
            inputStream.close();
            outputStream.close();
            recursiveRemove(currentFile, tableName);
            if (!buffer.renameTo(currentFile)) {
                throw new IOException();
            }
            currentFile = buffer;
            changes.clear();
            oldRecordNumber = recordNumber;
        } catch (IOException e) {
            printMessage(tableName + ": cannot commit changes: i/o error occurred");
            return 0;
        }
        return updated;
    }

    @Override
    public int rollback() {
        int canceled = recordNumber - oldRecordNumber;
        recordNumber = oldRecordNumber;
        changes.clear();
        return canceled;
    }

    protected void writeNextPair(DataOutputStream outputStream, String key, String value) throws IOException {
        byte[] keyBytes = key.getBytes("UTF-8");
        byte[] valueBytes = value.getBytes("UTF-8");
        outputStream.writeInt(keyBytes.length);
        outputStream.writeInt(valueBytes.length);
        outputStream.write(keyBytes);
        outputStream.write(valueBytes);
    }

    protected String[] readNextPair(DataInputStream inputStream) throws IOException {
        int keySize;
        int valueSize;
        try {
            keySize = inputStream.readInt();
            valueSize = inputStream.readInt();
            if (keySize < 1 || valueSize < 1 || inputStream.available() < keySize
                    || inputStream.available() < valueSize || inputStream.available() < keySize + valueSize) {
                throw new IOException("invalid string size");
            }
        } catch (IOException e) {
            return null;
        }
        byte[] keyBytes = new byte[keySize];
        byte[] valueBytes = new byte[valueSize];
        if (inputStream.read(keyBytes) != keySize || inputStream.read(valueBytes) != valueSize) {
            throw new IOException("unexpected end of file");
        }
        String[] pair = new String[2];
        pair[0] = new String(keyBytes, "UTF-8");
        pair[1] = new String(valueBytes, "UTF-8");
        return pair;
    }

    protected String readValue(String key) {
        if (currentFile == null) {
            return null;
        }
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(currentFile))) {
            String[] pair;
            while ((pair = readNextPair(inputStream)) != null) {
                if (pair[0].equals(key)) {
                    inputStream.close();
                    return pair[1];
                }
            }
            inputStream.close();
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}
