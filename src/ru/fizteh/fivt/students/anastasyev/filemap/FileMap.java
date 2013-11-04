package ru.fizteh.fivt.students.anastasyev.filemap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileMap {
    private File fileMap;
    private HashMap<String, String> elementHashMap = new HashMap<String, String>();
    private int ndirectory;
    private int nfile;

    public FileMap(String dbDir, int directory, int file) throws IOException {
        fileMap = new File(dbDir);
        ndirectory = directory;
        nfile = file;
        try {
            openFileMapWithCheck();
        } catch (FileNotFoundException e) {
            throw new IOException("File " + nfile + ".dat not found", e);
        } catch (IOException e) {
            throw new IOException(e.getMessage(), e);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
        if (elementHashMap.isEmpty()) {
            delete();
        }
    }

    private void openFileMapWithCheck() throws Exception {
        if (!fileMap.exists()) {
            return;
        }
        try (RandomAccessFile input = new RandomAccessFile(fileMap.toString(), "r")) {
            if (input.length() == 0) {
                return;
            }
            while (input.getFilePointer() < input.length()) {
                readWithCheck(input);
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(nfile + ".dat - File not found");
        } catch (IOException e) {
            throw new IOException(e.getMessage(), e);
        } catch (Exception e) {
            throw new Exception("In " + nfile + ".dat something goes very-very wrong", e);
        }
    }

    private void readWithCheck(RandomAccessFile input) throws IOException {
        int keyLength;
        int valueLength;
        try {
            keyLength = input.readInt();
            valueLength = input.readInt();
        } catch (IOException e) {
            throw new IOException("Error in key/value reading", e);
        }
        if (keyLength <= 0 || valueLength <= 0) {
            throw new IOException(nfile + ".dat has incorrect format");
        }
        try {
            byte[] keyBytes = new byte[keyLength];
            byte[] valueBytes = new byte[valueLength];
            input.read(keyBytes);
            input.read(valueBytes);
            if (keyBytes.length != keyLength || valueBytes.length != valueLength) {
                throw new IOException("Error in read strings in " + nfile + ".dat");
            }
            String key = new String(keyBytes);
            int hashcode = Math.abs(key.hashCode());
            if (hashcode % 16 != ndirectory || hashcode / 16 % 16 != nfile) {
                throw new IOException(ndirectory + ".dir" + File.separator + nfile + ".dat has wrong key: " + key);
            }
            String value = new String(valueBytes);
            elementHashMap.put(key, value);
        } catch (OutOfMemoryError e) {
            throw new IOException(nfile + ".dat has incorrect format", e);
        }
    }

    private void write(RandomAccessFile output, String key, String value) throws IOException {
        output.writeInt(key.getBytes("UTF-8").length);
        output.writeInt(value.getBytes("UTF-8").length);
        output.write(key.getBytes("UTF-8"));
        output.write(value.getBytes("UTF-8"));
    }

    public void save() throws IOException {
        if (!fileMap.getParentFile().exists()) {
            if (!fileMap.getParentFile().mkdir()) {
                throw new IOException("Can't create " + ndirectory + ".dir");
            }
        }
        if (!fileMap.exists()) {
            if (!fileMap.createNewFile()) {
                throw new IOException("Can't create " + nfile + ".dat");
            }
        }
        try (RandomAccessFile output = new RandomAccessFile(fileMap.toString(), "rw")) {
            output.setLength(0);
            Set<Map.Entry<String, String>> hashMapSet = elementHashMap.entrySet();
            for (Map.Entry<String, String> element : hashMapSet) {
                write(output, element.getKey(), element.getValue());
            }
        } catch (FileNotFoundException e) {
            throw new IOException("Can't find file to commit", e);
        } catch (Exception e) {
            throw new IOException("Can't commit FileMap", e);
        }
    }

    public String put(String newKey, String newValue) {
        return elementHashMap.put(newKey, newValue);
    }

    public String get(String key) {
        String value = elementHashMap.get(key);
        if (value == null) {
            return "not found";
        }
        return value;
    }

    public String remove(String key) {
        return elementHashMap.remove(key);
    }

    public boolean isEmpty() {
        return elementHashMap.isEmpty();
    }

    public void delete() throws IOException {
        if (!fileMap.getParentFile().exists()) {
            return;
        }
        if (fileMap.exists() && !fileMap.delete()) {
            throw new IOException("Can't remove empty fileMap");
        }
        if (fileMap.getParentFile().listFiles().length == 0) {
            if (!fileMap.getParentFile().delete()) {
                throw new IOException("Can't remove empty fileMaps directory");
            }
        }
    }

    public int size() {
        return elementHashMap.size();
    }
}
