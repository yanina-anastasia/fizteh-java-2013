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
        DataInputStream inputStream = new DataInputStream(new FileInputStream(currentFile));
        oldRecordNumber = 0;
        while (readNextString(inputStream) != null) {
            readNextString(inputStream);
            oldRecordNumber++;
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
        try {
            File buffer = new File(currentFile.getAbsolutePath() + '~');
            DataInputStream inputStream = new DataInputStream(new FileInputStream(currentFile));
            DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(buffer));
            String nextKey;
            String nextValue;
            while ((nextKey = readNextString(inputStream)) != null) {
                nextValue = readNextString(inputStream);
                if (changes.containsKey(nextKey)) {
                    nextValue = changes.get(nextKey);
                    changes.remove(nextKey);
                }
                if (nextValue != null) {
                    writeNextString(outputStream, nextKey);
                    writeNextString(outputStream, nextValue);
                }
            }
            Set<Entry<String, String>> entries = changes.entrySet();
            for (Entry<String, String> entry : entries) {
                if (entry.getValue() != null) {
                    writeNextString(outputStream, entry.getKey());
                    writeNextString(outputStream, entry.getValue());
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

    protected void writeNextString(DataOutputStream outputStream, String string) throws IOException {
        byte[] bytes = string.getBytes("UTF-8");
        outputStream.writeInt(bytes.length);
        outputStream.write(bytes);
    }

    protected String readNextString(DataInputStream inputStream) throws IOException {
        int keySize;
        try {
            keySize = inputStream.readInt();
            if (keySize < 1) {
                throw new IOException("invalid string size");
            }
        } catch (IOException e) {
            return null;
        }
        byte[] bytes = new byte[keySize];
        if (inputStream.read(bytes) != keySize) {
            throw new IOException("unexpected end of file");
        }
        return new String(bytes, "UTF-8");
    }

    protected String readValue(String key) {
        DataInputStream inputStream;
        try {
            inputStream = new DataInputStream(new FileInputStream(currentFile));
            String nextKey;
            String nextValue;
            while ((nextKey = readNextString(inputStream)) != null) {
                nextValue = readNextString(inputStream);
                if (nextKey.equals(key)) {
                    inputStream.close();
                    return nextValue;
                }
            }
            inputStream.close();
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}
